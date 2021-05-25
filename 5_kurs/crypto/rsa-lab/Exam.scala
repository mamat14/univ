#!/bin/sh
exec scala "$0" "$@"
!#
import java.nio.file.{Files, Paths}

import scala.io.StdIn.readLine
object Main extends App {
  val line = "ні" // readLine("Файл чи ні? Введіть \"так\" якщо файл.\n")
  val input: String = if(line == "так") {
    val fileName = readLine("Введіть ім'я файлу.\n")
    "qwewq"
  } else {
    readLine("Введіть текст.\n")
  }
  val frequencies = input.toList
    .groupBy(identity)
    .mapValues(_.length)
  println(frequencies.mkString("\n"))
  val singleBitLength = math.ceil(math.log(frequencies.keySet.size))
  val symbolToNumber = frequencies.keySet
    .foldLeft((Map[Char, Int](), 0))((map, symbol) => (map._1 + (symbol -> map._2), map._2 + 1))
    ._1

  println(symbolToNumber)
  val symbolToBitRepr = symbolToNumber.mapValues(_.toBinaryString)
  val maxLength = symbolToBitRepr.values.map(_.length).max
  println("Довжина кодування: " + maxLength)
  val finalMap = symbolToBitRepr.mapValues(s => "0" * (maxLength - s.length) + s)
  println("Мапа кодуваня: " + finalMap.mkString("\n"))
  val output = input.map(finalMap(_)).mkString
  println("Закодоване значення: " + output)
  if(line == "так") {
    val byteArray: Array[Byte] = BigInt(output, 2).toByteArray
    println(byteArray.mkString)
    val fileName = readLine("Введіть ім'я файлу результату.\n")
    Files.write(Paths.get(fileName), byteArray.prepended(maxLength.toByte))
  }
}
