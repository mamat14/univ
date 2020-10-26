# Документація
## Метод перший. LL(1) парсер
Використовується *власний* LL(1) парсер.
Граматика:

```text
E ::= T E'
E' ::= + T E'
E' ::= ''
T ::= F T'
T' ::= * F T'
T' ::= ''
F ::= ( E )
F ::= id K
F ::= num
K ::= ''
K ::= ( P )
P ::= E P'
P' ::= , E P'
P' ::= ''
```
Тут E - вираз. T - доданок. F - множник. інши - допоміжні нетермінали.


First, follow, nullable, та таблиця переходів наведені нижче.


<table class="pure-table pure-table-bordered">
    <thead>
    <tr id="firstFollowTableHead"><th>Nonterminal</th><th>Nullable?</th><th>First</th><th>Follow</th></tr>
    </thead>
    <tbody id="firstFollowTableRows"><tr></tr><tr><td nowrap="nowrap">S</td><td nowrap="nowrap">✖</td><td nowrap="nowrap">(, id, num</td><td nowrap="nowrap"></td></tr><tr></tr><tr><td nowrap="nowrap">E</td><td nowrap="nowrap">✖</td><td nowrap="nowrap">(, id, num</td><td nowrap="nowrap">), ,, $</td></tr><tr></tr><tr><td nowrap="nowrap">E'</td><td nowrap="nowrap">✔</td><td nowrap="nowrap">+</td><td nowrap="nowrap">), ,, $</td></tr><tr></tr><tr><td nowrap="nowrap">T</td><td nowrap="nowrap">✖</td><td nowrap="nowrap">(, id, num</td><td nowrap="nowrap">+, ), ,, $</td></tr><tr></tr><tr><td nowrap="nowrap">T'</td><td nowrap="nowrap">✔</td><td nowrap="nowrap">*</td><td nowrap="nowrap">+, ), ,, $</td></tr><tr></tr><tr><td nowrap="nowrap">F</td><td nowrap="nowrap">✖</td><td nowrap="nowrap">(, id, num</td><td nowrap="nowrap">+, *, ), ,, $</td></tr><tr></tr><tr><td nowrap="nowrap">K</td><td nowrap="nowrap">✔</td><td nowrap="nowrap">(</td><td nowrap="nowrap">+, *, ), ,, $</td></tr><tr></tr><tr><td nowrap="nowrap">P</td><td nowrap="nowrap">✖</td><td nowrap="nowrap">(, id, num</td><td nowrap="nowrap">)</td></tr><tr></tr><tr><td nowrap="nowrap">P'</td><td nowrap="nowrap">✔</td><td nowrap="nowrap">,</td><td nowrap="nowrap">)</td></tr></tbody>
</table>

<table>
    <thead>
    <tr id="llTableHead"><th></th><th>$</th><th>+</th><th>*</th><th>(</th><th>)</th><th>id</th><th>num</th></tr></thead>
    <tbody id="llTableRows"><tr></tr><tr><td nowrap="nowrap">S</td><td nowrap="nowrap"></td><td nowrap="nowrap"></td><td nowrap="nowrap"></td><td nowrap="nowrap">S ::= E $</td><td nowrap="nowrap"></td><td nowrap="nowrap">S ::= E $</td><td nowrap="nowrap">S ::= E $</td></tr><tr></tr><tr><td nowrap="nowrap">E</td><td nowrap="nowrap"></td><td nowrap="nowrap"></td><td nowrap="nowrap"></td><td nowrap="nowrap">E ::= T E'</td><td nowrap="nowrap"></td><td nowrap="nowrap">E ::= T E'</td><td nowrap="nowrap">E ::= T E'</td></tr><tr></tr><tr><td nowrap="nowrap">E'</td><td nowrap="nowrap">E' ::= ε</td><td nowrap="nowrap">E' ::= + T E'</td><td nowrap="nowrap"></td><td nowrap="nowrap"></td><td nowrap="nowrap">E' ::= ε</td><td nowrap="nowrap"></td><td nowrap="nowrap"></td></tr><tr></tr><tr><td nowrap="nowrap">T</td><td nowrap="nowrap"></td><td nowrap="nowrap"></td><td nowrap="nowrap"></td><td nowrap="nowrap">T ::= F T'</td><td nowrap="nowrap"></td><td nowrap="nowrap">T ::= F T'</td><td nowrap="nowrap">T ::= F T'</td></tr><tr></tr><tr><td nowrap="nowrap">T'</td><td nowrap="nowrap">T' ::= ε</td><td nowrap="nowrap">T' ::= ε</td><td nowrap="nowrap">T' ::= * F T'</td><td nowrap="nowrap"></td><td nowrap="nowrap">T' ::= ε</td><td nowrap="nowrap"></td><td nowrap="nowrap"></td></tr><tr></tr><tr><td nowrap="nowrap">F</td><td nowrap="nowrap"></td><td nowrap="nowrap"></td><td nowrap="nowrap"></td><td nowrap="nowrap">F ::= ( E )</td><td nowrap="nowrap"></td><td nowrap="nowrap">F ::= id K</td><td nowrap="nowrap">F ::= num</td></tr><tr></tr><tr><td nowrap="nowrap">K</td><td nowrap="nowrap">K ::= ε</td><td nowrap="nowrap">K ::= ε</td><td nowrap="nowrap">K ::= ε</td><td nowrap="nowrap">K ::= ( E )</td><td nowrap="nowrap">K ::= ε</td><td nowrap="nowrap"></td><td nowrap="nowrap"></td></tr></tbody>
</table>

Приклад роботи програми:
```text
Enter global variables in form: x=2;y=3.1;
PI=3.14
Enter expression to evaluate. Example: sin(3.14) + pow(2, 3)
sin(PI)
S
|-E
  |-T
    |-FId
      |-sin
      |-KE
        |-P
          |-E
            |-T
              |-FId
                |-PI
                |-KEmpty
              |-T1Empty
            |-E1Empty
          |-P1Empty
    |-T1Empty
  |-E1Empty
Enter variables in form: x=2;y=3.1. Press Enter to ignore.

0.0015926529164868282
Enter expression to evaluate. Example: sin(3.14) + pow(2, 3)
pow(sin(x), 2) + pow(cos(x), 2)
S
|-E
  |-T
    |-FId
      |-pow
      |-KE
        |-P
          |-E
            |-T
              |-FId
                |-sin
                |-KE
                  |-P
                    |-E
                      |-T
                        |-FId
                          |-x
                          |-KEmpty
                        |-T1Empty
                      |-E1Empty
                    |-P1Empty
              |-T1Empty
            |-E1Empty
          |-P1Rep
            |-E
              |-T
                |-FNum
                  |-2.0
                |-T1Empty
              |-E1Empty
            |-P1Empty
    |-T1Empty
  |-E1Plus
    |-T
      |-FId
        |-pow
        |-KE
          |-P
            |-E
              |-T
                |-FId
                  |-cos
                  |-KE
                    |-P
                      |-E
                        |-T
                          |-FId
                            |-x
                            |-KEmpty
                          |-T1Empty
                        |-E1Empty
                      |-P1Empty
                |-T1Empty
              |-E1Empty
            |-P1Rep
              |-E
                |-T
                  |-FNum
                    |-2.0
                  |-T1Empty
                |-E1Empty
              |-P1Empty
      |-T1Empty
    |-E1Empty
Enter variables in form: x=2;y=3.1. Press Enter to ignore.
x=1.1
1.0
```
## Метод другий. Придумав сам
### Опис
Рекурсивно:
Основний switch statement.
```text
switch(токен) {
    якщо це ( => numberStack.push(рекурсивноОбрахувати)
    якщо це + | - | * | / | "pow" | "sin" то 
       якщо оператори непусті та (precedence(токен) <= precedence(операторНаСтеку)) 
            обрахувати стек
       додати оператор на вершину стека
    якщо це змінна => numberStack.push(знайти(токен))
    якщо це число => numberStack.push(токен)
    якщо це ) | 'кінець вводу' => обрахувати стек
}
```
Приклад обрахунку
```text
2 pow (4 + sin 2)
Eval
Stack()
Stack()
--------------------------------------------------------------------------------
Eval
Stack(Num(2.0))
Stack()
--------------------------------------------------------------------------------
Eval
Stack(Num(2.0))
Stack(Id(pow))
--------------------------------------------------------------------------------
Eval //старт рекурсії, тобто обраховуємо (4 + sin 2)
Stack()
Stack()
--------------------------------------------------------------------------------
Eval
Stack(Num(4.0))
Stack()
--------------------------------------------------------------------------------
Eval
Stack(Num(4.0))
Stack(Plus)
--------------------------------------------------------------------------------
Eval
Stack(Num(4.0))
Stack(Id(sin), Plus)
--------------------------------------------------------------------------------
Eval
Stack(Num(2.0), Num(4.0))
Stack(Id(sin), Plus)
--------------------------------------------------------------------------------
Reduce
Stack(Num(2.0), Num(4.0))
Stack(Id(sin), Plus)
--------------------------------------------------------------------------------
Reduce
Stack(Num(0.9092974268256817), Num(4.0))
Stack(Plus)
--------------------------------------------------------------------------------
Reduce
Stack(Num(4.909297426825682))
Stack()
--------------------------------------------------------------------------------
Eval
Stack(Num(4.909297426825682), Num(2.0))
Stack(Id(pow))
--------------------------------------------------------------------------------
Reduce
Stack(Num(4.909297426825682), Num(2.0))
Stack(Id(pow))
--------------------------------------------------------------------------------
Reduce
Stack(Num(30.05009041917967))
Stack()
--------------------------------------------------------------------------------
30.05009041917967

Process finished with exit code 0

```
