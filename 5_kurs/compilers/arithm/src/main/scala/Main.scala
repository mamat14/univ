import java.io.PushbackReader

import scala.collection.mutable
import scala.io.Source
sealed trait Expr

sealed trait Token
case object Plus extends Token
case object Minus extends Token
case object Prod extends Token
case object Div extends Token
case class Id(s: String) extends Token
case class Num(n: Number) extends Token
case object LParen extends Token
case object RParen extends Token
case object EOF extends Token

sealed trait K extends Expr
case object KEmpty extends K
case class KE(e: E) extends K

sealed trait F extends Expr
case class FId(id: String, k: K) extends F
case class FNum(num: Number) extends F
case class FExpr(e: E) extends F

sealed trait T1
case object T1Empty extends T1
case class T1Prod(f: F, t1: T1) extends T1
case class T1Div(f: F, t1: T1) extends T1

case class T(f: F, t1: T1) extends Expr

sealed trait E1 extends Expr
case object E1Empty extends E1
case class E1Plus(t: T, e1: E1) extends E1
case class E1Minus(t: T, e1: E1) extends E1

case class E(t: T, e1: E1) extends Expr
case class S(e: E) extends Expr

class Tokens(input: PushbackReader) {
  @scala.annotation.tailrec
  final def nextToken(): Token = input.read() match {
    case '+' => Plus
    case '-' =>
      val next = input.read().toChar
      input.unread(next)
      if(next.isDigit) {
        Num(-readNumber())
      } else {
        Minus
      }
    case '*' => Prod
    case '/' => Div
    case '(' => LParen
    case ')' => RParen
    case c if c == -1 || c == '\n' => EOF
    case c if c.toChar.isDigit =>
      input.unread(c)
      Num(readNumber())
    case c if c.toChar.isLetter =>
      input.unread(c)
      readLiteral() match {
        case literal => Id(literal)
      }
    case c if c.toChar.isWhitespace =>
      nextToken()
    case err =>
      throw new Exception(s"Unexpected symbol: ${ new String(Array(err.toChar)) }")
  }

  private def readNumber(): Double = {
    var c: Char = 0
    val sequence = mutable.ArrayBuffer[Char]()
    do {
      c = input.read().toChar
      sequence.addOne(c)
    } while (c.isDigit)
    if (c != '.') {
      sequence.remove(sequence.size - 1)
      input.unread(c)
      sequence.mkString.toDouble
    } else {
      do {
        c = input.read().toChar
        sequence.addOne(c)
      } while (c.isDigit)
      sequence.remove(sequence.size - 1)
      input.unread(c)
      sequence.mkString.toDouble
    }
  }

  private def readLiteral(): String = {
    var c: Char = 0
    val sequence = mutable.ArrayBuffer[Char]()
    do {
      c = input.read().toChar
      sequence.addOne(c)
    } while (c.isLetterOrDigit)
    sequence.remove(sequence.size - 1)
    input.unread(c)
    sequence.mkString
  }

}

class Parser(tokens: Tokens) {
  private var curTok = tokens.nextToken()
  private var eofPassed = false

  private def advance(): Unit = {
    if (eofPassed) {
      throw new Exception("EOF eaten.")
    }
    curTok = tokens.nextToken()
  }

  def eat(t: Token): Unit = {
    if (curTok == EOF) {
      eofPassed = true
    } else if (curTok == t) {
      advance()
    } else {
      throw new Exception(s"Unexpected token $curTok. Expected $t.")
    }
  }


  private def pK(): K = curTok match {
    case _@(EOF | Plus | Minus | Prod | Div | RParen) => KEmpty
    case LParen => eat(LParen)
      val res = KE(pE())
      eat(RParen)
      res
    case err => throw new Exception(s"Expected EOF, +, -, *, /, ), ( in K. Got $err ")
  }

  private def pF(): F = curTok match {
    case Id(variable) => advance(); FId(variable, pK())
    case Num(number) => advance(); FNum(number)
    case LParen =>
      eat(LParen)
      val res = FExpr(pE())
      eat(RParen)
      res
    case err => throw new Exception(s"Expected id,num, (, call in F. Got $err.")
  }

  private def pT1(): T1 = curTok match {
    case _@(Plus | Minus | RParen | EOF) => T1Empty
    case Prod => eat(Prod); T1Prod(pF(), pT1())
    case Div => eat(Div); T1Div(pF(), pT1())
    case err => throw new Exception(s"Expected +,-,*, / or EOF in T1. Got $err.")
  }

  private def pE1(): E1 = curTok match {
    case _@(RParen | EOF) => E1Empty
    case Plus => eat(Plus); E1Plus(pT(), pE1())
    case Minus => eat(Minus); E1Minus(pT(), pE1())
    case err => throw new Exception(s"Expected +,- or ) in E'. Got $err")
  }

  private def pT(): T = curTok match {
    case _@(Id(_) | Num(_) | LParen) => T(pF(), pT1())
    case err => throw new Exception(s"Expected id, num or ( in T. Got $err.")
  }

  private def pE(): E = curTok match {
    case _@(Id(_) | Num(_) | LParen) => E(pT(), pE1())
    case err => throw new Exception(s"Expected id,num or ( at start of E. Got: $err")
  }

  final def pS(): S = curTok match {
    case _@(Id(_) | Num(_) | LParen) =>
      val res = S(pE())
      eat(EOF)
      res
    case token => throw new Exception(s"Unexpected token $token in rule S.")
  }
}

class Evaluator(variables: Map[String, Double]) {
  def eval(expr: Expr): Double = expr match {
    case FId(id, k) => k match {
      case KEmpty => variables(id)
      case KE(e) => id match {
        case "sin" => math.sin(eval(e))
        case "cos" => math.cos(eval(e))
        case "tg" => math.tan(eval(e))
        case unknown => throw new Exception(s"Unknown function $unknown.")
      }
    }
    case FNum(num) => num.doubleValue()
    case FExpr(e) => eval(e)
    case T(f, T1Empty) => eval(f)
    case T(f, t1: T1Prod) => eval(T(FNum(eval(f) * eval(t1.f)), t1.t1))
    case T(f, t1: T1Div) => eval(T(FNum(eval(f) / eval(t1.f)), t1.t1))
    case E(t, E1Empty) => eval(t)
    case E(t, e1: E1Plus) => eval(E(T(FNum(eval(t) + eval(e1.t)), T1Empty), e1.e1))
    case E(t, e1: E1Minus) => eval(E(T(FNum(eval(t) - eval(e1.t)), T1Empty), e1.e1))
    case S(e) => eval(e)
  }
}

object Main {
  val input: PushbackReader = new PushbackReader(Source.stdin.reader())
  val tokens = new Tokens(input)
  val evaluator = new Evaluator(Map())

  def main(args: Array[String]): Unit = {
    while (true) {
      val parser = new Parser(tokens)
      val res = parser.pS()
      pprint.pprintln(res)
      val value = evaluator.eval(res)
      pprint.pprintln(value)
    }
  }
}
