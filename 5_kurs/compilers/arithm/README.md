# Docs

Використовується LL(1) парсер.
Граматика:

```text
S ::= E $
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
K ::= ( E )
```
Тут E - вираз. T - доданок. F - множник. інши - допоміжні нетермінали.


First, follow, nullable, та таблиця переходів наведені нижче.

<table class="pure-table pure-table-bordered">
    <thead>
    <tr id="firstFollowTableHead"><th>Nonterminal</th><th>Nullable?</th><th>First</th><th>Follow</th></tr>
    </thead>
    <tbody id="firstFollowTableRows"><tr></tr><tr><td nowrap="nowrap">S</td><td nowrap="nowrap">✖</td><td nowrap="nowrap">(, id</td><td nowrap="nowrap"></td></tr><tr></tr><tr><td nowrap="nowrap">E</td><td nowrap="nowrap">✖</td><td nowrap="nowrap">(, id</td><td nowrap="nowrap">), $</td></tr><tr></tr><tr><td nowrap="nowrap">E'</td><td nowrap="nowrap">✔</td><td nowrap="nowrap">+</td><td nowrap="nowrap">), $</td></tr><tr></tr><tr><td nowrap="nowrap">T</td><td nowrap="nowrap">✖</td><td nowrap="nowrap">(, id</td><td nowrap="nowrap">+, ), $</td></tr><tr></tr><tr><td nowrap="nowrap">T'</td><td nowrap="nowrap">✔</td><td nowrap="nowrap">*</td><td nowrap="nowrap">+, ), $</td></tr><tr></tr><tr><td nowrap="nowrap">F</td><td nowrap="nowrap">✖</td><td nowrap="nowrap">(, id</td><td nowrap="nowrap">+, *, ), $</td></tr><tr></tr><tr><td nowrap="nowrap">K</td><td nowrap="nowrap">✔</td><td nowrap="nowrap">(</td><td nowrap="nowrap">+, *, ), $</td></tr></tbody>
</table>
<table class="pure-table pure-table-bordered" border="1">
    <thead>
    <tr id="llTableHead"><th></th><th>$</th><th>+</th><th>*</th><th>(</th><th>)</th><th>id</th></tr></thead>
    <tbody id="llTableRows"><tr></tr><tr><td nowrap="nowrap">S</td><td nowrap="nowrap"></td><td nowrap="nowrap"></td><td nowrap="nowrap"></td><td nowrap="nowrap">S ::= E $</td><td nowrap="nowrap"></td><td nowrap="nowrap">S ::= E $</td></tr><tr></tr><tr><td nowrap="nowrap">E</td><td nowrap="nowrap"></td><td nowrap="nowrap"></td><td nowrap="nowrap"></td><td nowrap="nowrap">E ::= T E'</td><td nowrap="nowrap"></td><td nowrap="nowrap">E ::= T E'</td></tr><tr></tr><tr><td nowrap="nowrap">E'</td><td nowrap="nowrap">E' ::= ε</td><td nowrap="nowrap">E' ::= + T E'</td><td nowrap="nowrap"></td><td nowrap="nowrap"></td><td nowrap="nowrap">E' ::= ε</td><td nowrap="nowrap"></td></tr><tr></tr><tr><td nowrap="nowrap">T</td><td nowrap="nowrap"></td><td nowrap="nowrap"></td><td nowrap="nowrap"></td><td nowrap="nowrap">T ::= F T'</td><td nowrap="nowrap"></td><td nowrap="nowrap">T ::= F T'</td></tr><tr></tr><tr><td nowrap="nowrap">T'</td><td nowrap="nowrap">T' ::= ε</td><td nowrap="nowrap">T' ::= ε</td><td nowrap="nowrap">T' ::= * F T'</td><td nowrap="nowrap"></td><td nowrap="nowrap">T' ::= ε</td><td nowrap="nowrap"></td></tr><tr></tr><tr><td nowrap="nowrap">F</td><td nowrap="nowrap"></td><td nowrap="nowrap"></td><td nowrap="nowrap"></td><td nowrap="nowrap">F ::= ( E )</td><td nowrap="nowrap"></td><td nowrap="nowrap">F ::= id K</td></tr><tr></tr><tr><td nowrap="nowrap">K</td><td nowrap="nowrap">K ::= ε</td><td nowrap="nowrap">K ::= ε</td><td nowrap="nowrap">K ::= ε</td><td nowrap="nowrap">K ::= ( E )</td><td nowrap="nowrap">K ::= ε</td><td nowrap="nowrap"></td></tr></tbody>
</table>

