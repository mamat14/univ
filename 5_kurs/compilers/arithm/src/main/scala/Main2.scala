import java.io.PushbackReader

import scala.collection.mutable
import scala.io.Source

case class Operator(t: Token, precedence: Int)

object Main2 extends App {
  val arity = Map[Token, Int](
    Plus -> 2,
    Minus -> 2,
    Prod -> 2,
    Div -> 2,
    Id("pow") -> 2,
    Id("sin") -> 1)

  val evaluator = Map[Token, List[Num] => Double](
    Plus -> { case List(n1, n2) => n1.n.doubleValue() + n2.n.doubleValue() },
    Minus -> { case List(n1, n2) => n1.n.doubleValue() - n2.n.doubleValue() },
    Prod -> { case List(n1, n2) => n1.n.doubleValue() * n2.n.doubleValue() },
    Div -> { case List(n1, n2) => n1.n.doubleValue() / n2.n.doubleValue() },
    Id("pow") -> { case List(n1, n2) => math.pow(n1.n.doubleValue(), n2.n.doubleValue()) },
    Id("sin") -> { case List(n1) => math.sin(n1.n.doubleValue()) }
    )

  val precedence = Map[Token, Int](
    Plus -> 1,
    Minus -> 1,
    Prod -> 2,
    Div -> 2,
    Id("pow") -> 3,
    Id("sin") -> 4)

  @scala.annotation.tailrec
  def reduceStack(numbers: mutable.Stack[Num], operators: mutable.Stack[Token]): Unit = {
    println("Reduce")
    println(numbers)
    println(operators)
    println("-" * 80)
    operators.headOption match {
      case Some(_) =>
        val operator = operators.pop()
        val opArity = arity(operator)
        val res = evaluator(operator)(List.fill(opArity)(numbers.pop()).reverse)
        numbers.push(Num(res))
        reduceStack(numbers, operators)
      case None => ()
    }
  }

  def eval(tokens: Tokens, variables: Map[String, Double]): Double = {
    var curToken: Token = null
    val numberStack = mutable.Stack[Num]()
    val operatorStack = mutable.Stack[Token]()
    do {
      curToken = tokens.nextToken()
      println("Eval")
      println(numberStack)
      println(operatorStack)
      println("-" * 80)
      curToken match {
        case LParen => numberStack.push(Num(eval(tokens, variables)))
        case operator@(Plus | Minus | Prod | Div | Id("pow") | Id("sin")) =>
          val operatorOption = operatorStack.headOption
          if (operatorOption.isDefined && precedence(operator) <= precedence(operatorOption.get)) {
            reduceStack(numberStack, operatorStack)
          }
          operatorStack.push(operator)
        case e: Id => numberStack.push(Num(variables(e.s)))
        case e: Num => numberStack.push(e)
        case _ @ RParen | EOF => reduceStack(numberStack, operatorStack)
      }
    }

    while (curToken != RParen && curToken != EOF)
    numberStack.pop().n.doubleValue()
  }

  val input: PushbackReader = new PushbackReader(Source.stdin.reader())
  val tokens = new Tokens(input)
  println(eval(tokens, Map()))
}
