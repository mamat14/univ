#!/bin/sh
exec scala "$0" "$@"
!#
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import java.io.PrintWriter

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

_  def createLine(a: Array[String]): Array[String] = {
    if(a.length == 32) {
      Array(
        a(0), //номер кладовой
        a(1), //балансовый счет
        a(17), //заказ
        a(2), //инвентарный номер
        a(3), //ном Н
        a(4) + a(12) + a(20) + a(28), //название + тема
        a(5), // цена
        a(13), //кол-во
        a(14), //ед. изм
        a(6), //стоимость
        a(15) //дата
        )
    } else if (a.length == 24) {
      Array(
        a(0),
        a(1),
        a(17),
        a(2), //инвентарный номер
        a(3), //ном Н
        a(4) + a(12) + a(20),
        a(5), // цена
        a(13), //кол-во
        a(14), //ед. изм
        a(6), //стоимость
        a(15) //дата
      )
    } else if(a.length == 40) {
      Array(
        a(0), //номер кладовой
        a(1), //балансовый счет
        a(17), //заказ
        a(2), //инвентарный номер
        a(3), //nom n
        a(4) + a(12) + a(20) + a(28) + a(36), //название + тема
        a(5), // цена
        a(13), //кол-во
        a(14), //ед. изм
        a(6), //стоимость
        a(15) //дата
        )
    } else {
      throw new Exception("Неожиданный размер")
    }
  }

  def extractCells(string: String): Array[String] = {
    Array(
      string.substring(1,4),
      string.substring(5, 10),
      string.substring(11, 26),
      string.substring(27, 32),
      string.substring(33, 83),
      string.substring(84, 97),
      string.substring(98, 111),
      string.substring(112, 120)
    )
  }

  val исключения = List("EX-FH25:")
  val byteArray = Files.readAllBytes(Paths.get("/Users/dmytriim/Documents/SO1WSQ09.TXT"))
  val res26 = new String(byteArray, "Cp1251")
  new PrintWriter("/Users/dmytriim/Documents/SO1WSQ09-UTF-8.TXT", StandardCharsets.UTF_8) { write(res26); close() }

  val exculed = List("                                     Ведомость спецоборудования   ",
                      "  :     :               :     :                     ТЕМА                         :",
                     "КЛА:Б/СЧ.: ИНВЕНТАРНЫЙ N :НОМ.N:          HАИМЕHОВАHИЕ СПЕЦОБОРУДОВАНИЯ           :     ЦЕНА    :  СТОИМОСТЬ  :   ",
                     ":ВАЯ:ЗАКАЗ:               :     :                                                  :",
                     ":ДО :     :",
                     "---------------------------------------",
                     "                                                                                             ",
                     "ВСЕГО   57338666.94         ",
                     "К-ВО С/О      6295          ")
  val q2: Array[String] = res26
    .split("\n")
    .filter(_.length != 2)
    .filterNot(s => exculed.exists(s.contains))
    .flatMap(extractCells)


  val fullRow: mutable.ArrayBuffer[mutable.ArrayBuffer[String]] = ArrayBuffer()
  for{
    cellRow <- q2.sliding(8,8)
  } {
    if(!cellRow(0).isBlank) {
      fullRow.addOne(ArrayBuffer.from(cellRow))
    } else {
      fullRow.last.addAll(cellRow)
    }
  }

  val q: Seq[Array[String]] = fullRow.map(_.toArray)
    .toList
    .map(createLine)
    .map(_.zipWithIndex.flatMap{ case (s, i) => if(i != 5) Array(s.filterNot(_.isWhitespace)) else {
      val q11 = s.split(" {4}[ ]+").map(_.trim())
      val q12 = q11.filterNot(_.isBlank)
      val res = if(q12.length == 1) {
        q12 :+ ""
      } else if(q12.length >= 2) {
        q12.take(2)
      } else {
        Array("", "")
      }
      if(res.size != 2) {
        throw new Exception("Error length")
      } else {
        res
      }
    }})

  val колонки = Array("Номер кладовой", "Балансовый счет", "Заказ", "Инвентарный номер", "НОМ.N", "Название", "Тема", "Цена", "Количество", "Единица измерения", "Стоимость", "Дата")

  new PrintWriter("/Users/dmytriim/Documents/SO1WSQ09-PROCESSED.TXT", StandardCharsets.UTF_8) { write((колонки +: q).map(_.map(_.replace("\"", "\"\"")).map("\"" + _ + "\"")).map(_.mkString(";")).mkString("\n")); close() }
}
