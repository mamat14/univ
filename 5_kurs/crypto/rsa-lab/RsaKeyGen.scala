#!/bin/sh
exec scala "$0" "$@"
!#
import scala.util.Random
import BigInt._

object BigIntHelpers {
  implicit class BigIntOps(i: BigInt) {
    def concat(j: BigInt, jBits: Int): BigInt = (i << jBits) ^ j
  }
}

class MGF1() {
  def generateMask(mgfSeedBigInt: BigInt, maskLenBits: Int): BigInt = {
    val digest = java.security.MessageDigest.getInstance("SHA-1")
    val maskLen = maskLenBits / 8
    val mgfSeed = mgfSeedBigInt.toByteArray
    val hashCount = (maskLen + digest.getDigestLength - 1) / digest.getDigestLength
    var mask = new Array[Byte](0)
    for ( i <- 0 until hashCount ) {
      digest.update(mgfSeed)
      digest.update(new Array[Byte](3))
      digest.update(i.toByte)
      val hash = digest.digest
      val c: Array[Byte] = new Array[Byte](mask.length + hash.length)
      System.arraycopy(mask, 0, c, 0, mask.length)
      System.arraycopy(hash, 0, c, mask.length, hash.length)
      mask = c
    }
    val output = new Array[Byte](maskLen)
    System.arraycopy(mask, 0, output, 0, output.length)
    BigInt(1, output)
  }
}

trait AsymProtocol[P, S] {
  type PublicPart = P
  type Key = AKey[P, S]
  val nbits: Int

  def gen(): Key

  def encrypt(plain: BigInt, p: PublicPart): BigInt

  def decrypt(cipher: BigInt, k: Key): BigInt
}

case class Secret(d: BigInt)
case class Public(n: BigInt, e: BigInt)
case class AKey[P, S](public: P, secret: S)

class RSA(bitLengthC: Int) extends AsymProtocol[Public, Secret] {
  override val nbits: Int = bitLengthC

  def gen(): Key = {
    val random = new Random()
    val p = BigInt.probablePrime(nbits, random)
    val q = BigInt.probablePrime(nbits, random)
    val n = p * q
    val λ = (p - 1) * (q - 1) / (p gcd q)
    val e = 65537
    val d = e.modInverse(λ)
    AKey(Public(n, e), Secret(d))
  }

  def encrypt(plain: BigInt, public: Public): BigInt = plain modPow (public.e, public.n)

  def decrypt(cipher: BigInt, key: Key): BigInt = cipher modPow (key.secret.d, key.public.n)
}

object OAEP {
  self =>
  import BigIntHelpers._
  def withOAEP[P, S](orig: AsymProtocol[P, S]): AsymProtocol[P, S] = new AsymProtocol[P, S] {
    val nbits: Int = orig.nbits
    val k0 = 80
    val k1 = 80
    val random = new Random()
    val mgf1 = new MGF1()
    val G: BigInt => BigInt = mgf1.generateMask(_, nbits - k0)
    val H: BigInt => BigInt = mgf1.generateMask(_, k0)

    override def gen(): Key = orig.gen()

    override def encrypt(plain: BigInt, p: PublicPart): BigInt = {
      val a1 = plain << k1
      val r = BigInt(k0, random)
      val X = a1 ^ G(r)
      val Y = r ^ H(X)
      val XY = X.concat(Y, k0)
      orig.encrypt(XY, p)
    }

    override def decrypt(cipher: BigInt, k: Key): BigInt = {
      val XY = orig.decrypt(cipher, k)
      val X = XY >> k0
      val Y = XY ^ (X << k0)
      val r = Y ^ H(X)
      val a1 = X ^ G(r)
      if (a1.mod(1 << k0) != 0) {
        throw new Exception("Message corrupted.")
      }
      a1 >> k1
    }
  }

  implicit class OAEPOps[P, S](asymProtocol: AsymProtocol[P, S]) {
    def withOaep: AsymProtocol[P, S] = self.withOAEP(asymProtocol)
  }
}

object Main extends App {
  import OAEP._
  val rsa = new RSA(256)
  val rsaOaep = rsa.withOaep

  private def checkRandomString[T, U](asym: AsymProtocol[T, U], key: AKey[T,U]): Unit = {
    val message = BigInt(256, new Random())
    val encrypted = asym.encrypt(message, key.public)
    val decrypted = asym.decrypt(encrypted, key)
    if(message != decrypted) {
      throw new Exception("oops plain != encrypted")
    }
  }

  var start,finish: Long = 0
  val iterations = 100000
  val mbits = 256 * iterations / 1000000
  start = System.currentTimeMillis()
  val keyRsa = rsa.gen()
  for{_ <- 0 until iterations} {
    checkRandomString(rsa, keyRsa)
  }
  finish = System.currentTimeMillis()
  println(s"RSA IS: ${(1000 * mbits) / (finish-start).toDouble} Mb/sec ")

  start = System.currentTimeMillis()
  val keyOaep = rsaOaep.gen()
  for{_ <- 0 until iterations} {
    checkRandomString(rsaOaep, keyOaep)
  }
  finish = System.currentTimeMillis()
  println(s"RSA-OAEP IS: ${(1000* mbits) / (finish-start).toDouble} Mb/sec ")


}
