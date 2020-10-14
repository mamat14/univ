import {p} from './main'
var numeral = require('numeral');

function run(n : number) {

    let transitionalProbabilities = new Array(n);

    for (let i = 0; i < n; ++i) {
        transitionalProbabilities[i] = new Array(n)
        for (let j = 0; j < n; ++j) {
            transitionalProbabilities[i][j] = p(i, j);
        }
    }

   return transitionalProbabilities
}


const matrix = run(10);
const str = matrix
    .map(row => row
        .map(el => numeral(el).format('0.00')))
    .map(row => row.join(', '))
    .join('\n');

console.log(str);