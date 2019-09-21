import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'CalculatorICP';
  currentNumber = '0';
  previousNumber = null;
  operator = null;
  waitForSecondNumber = false;
  // code to store numbers from users
  num(v: string) {
    console.log(v);
    if (this.waitForSecondNumber) {
      this.currentNumber = v;
      this.waitForSecondNumber = false;
    } else {
      this.currentNumber === '0' ? this.currentNumber = v : this.currentNumber += v;

    }
  }
  // code to perform operations
  doCalculation(op , secondOp) {
    switch (op) {
      case '+':
        return this.previousNumber += secondOp;
      case '-':
        return this.previousNumber -= secondOp;
      case '*':
        return this.previousNumber *= secondOp;
      case '/':
        return this.previousNumber /= secondOp;
      case '=':
        return secondOp;
    }
  }
  // code to get the operator
  operation(op: string) {
    console.log(op);

    if (this.previousNumber === null) {
      this.previousNumber = Number(this.currentNumber);

    } else if (this.operator) {
      const result = this.doCalculation(this.operator , Number(this.currentNumber))
      this.currentNumber = String(result);
      this.previousNumber = result;
    }
    this.operator = op;
    this.waitForSecondNumber = true;

    console.log(this.previousNumber);

  }
  // code to reset everything
  clear() {
    this.currentNumber = '0';
    this.previousNumber = null;
    this.operator = null;
    this.waitForSecondNumber = false;
  }
}
