package com.example.calculationapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView display;
    private double operand1 = Double.NaN;
    private String pendingOp = "=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);

        // 1) Цифры 0–9
        int[] digitIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
                R.id.btn8, R.id.btn9
        };
        View.OnClickListener digitListener = v -> {
            String d = ((Button) v).getText().toString();
            String cur = display.getText().toString();
            if ("0".equals(cur)) display.setText(d);
            else display.append(d);
        };
        for (int id : digitIds) {
            findViewById(id).setOnClickListener(digitListener);
        }

        // 2) Точка
        findViewById(R.id.btnDot).setOnClickListener(v -> {
            String cur = display.getText().toString();
            if (!cur.contains(".")) display.append(".");
        });

        // 3) Операции +, −, ×, ÷ и =
        int[] opIds = {
                R.id.btnAdd, R.id.btnSub,
                R.id.btnMul, R.id.btnDiv,
                R.id.btnEq
        };
        View.OnClickListener opListener = v -> {
            String op = ((Button) v).getText().toString();
            String curText = display.getText().toString();
            double value;
            try {
                value = Double.parseDouble(curText);
            } catch (NumberFormatException e) {
                value = 0;
            }
            performOperation(value, op);
            pendingOp = op;
            // после любой операции (кроме "=") сбрасываем поле для ввода второго числа
            if (!"=".equals(op)) {
                display.setText("0");
            }
        };
        for (int id : opIds) {
            findViewById(id).setOnClickListener(opListener);
        }

        // 4) Сброс «C»
        findViewById(R.id.btnC).setOnClickListener(v -> {
            operand1 = Double.NaN;
            pendingOp = "=";
            display.setText("0");
        });
    } // ← эта скобка закрывает onCreate()

    /**
     * Выполняет накопленную операцию pendingOp над operand1 и newValue,
     * сохраняет результат в operand1 и выводит его на экран.
     */
    private void performOperation(double newValue, String op) {
        if (Double.isNaN(operand1)) {
            operand1 = newValue;
        } else {
            switch (pendingOp) {
                case "=":
                    operand1 = newValue;
                    break;
                case "+":
                    operand1 += newValue;
                    break;
                case "−": case "-":
                    operand1 -= newValue;
                    break;
                case "×": case "*":
                    operand1 *= newValue;
                    break;
                case "÷": case "/":
                    if (newValue == 0) {
                        display.setText("Error");
                        return;
                    }
                    operand1 /= newValue;
                    break;
            }
        }
        // Показываем без дробной точки, если число целое
        if (operand1 == (long) operand1) {
            display.setText(String.format("%d", (long) operand1));
        } else {
            display.setText(String.valueOf(operand1));
        }
    }
}
