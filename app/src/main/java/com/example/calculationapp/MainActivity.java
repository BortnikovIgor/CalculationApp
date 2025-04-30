package com.example.calculationapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private TextView display;
    private double operand1 = Double.NaN;
    private String pendingOp = "=";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // === Toolbar + Drawer ===
        drawerLayout = findViewById(R.id.drawer_layout);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        toolbar.setNavigationOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START)
        );

        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_about) {
                startActivity(new Intent(this, AboutActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // === Логика калькулятора ===
        display = findViewById(R.id.display);

        // 1) Цифры
        int[] digitIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
                R.id.btn8, R.id.btn9
        };
        View.OnClickListener digitListener = v -> {
            String d   = ((Button)v).getText().toString();
            String cur = display.getText().toString();
            if ("0".equals(cur)) {
                display.setText(d);
            } else {
                display.append(d);
            }
        };
        for (int id : digitIds) findViewById(id).setOnClickListener(digitListener);

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
            String op      = ((Button)v).getText().toString();
            String curText = display.getText().toString();
            double value;
            try {
                value = Double.parseDouble(curText);
            } catch (NumberFormatException e) {
                value = 0;
            }
            performOperation(value, op);
            pendingOp = op;
            if (!"=".equals(op)) {
                display.setText("0");
            }
        };
        for (int id : opIds) findViewById(id).setOnClickListener(opListener);

        // 4) Сброс «C» — сюда вставляем Snackbar
        findViewById(R.id.btnC).setOnClickListener(v -> {
            operand1 = Double.NaN;
            pendingOp = "=";
            display.setText("0");

            // показываем сообщение о сбросе
            Snackbar.make(
                    findViewById(R.id.buttonGrid),  // ваш контейнер с кнопками
                    "Калькулятор сброшен",          // текст
                    Snackbar.LENGTH_SHORT           // длительность
            ).show();
        });
    }

    private void performOperation(double newValue, String op) {
        if (Double.isNaN(operand1)) {
            operand1 = newValue;
        } else {
            switch (pendingOp) {
                case "=": operand1 = newValue; break;
                case "+": operand1 += newValue; break;
                case "−": case "-": operand1 -= newValue; break;
                case "×": case "*": operand1 *= newValue; break;
                case "÷": case "/":
                    if (newValue != 0) operand1 /= newValue;
                    else {
                        display.setText("Error");
                        return;
                    }
                    break;
            }
        }
        if (operand1 == (long)operand1) {
            display.setText(String.format("%d", (long)operand1));
        } else {
            display.setText(String.valueOf(operand1));
        }
    }
}
