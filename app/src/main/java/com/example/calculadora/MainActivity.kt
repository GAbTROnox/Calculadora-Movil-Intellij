package com.example.calculadora

import android.os.Bundle
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var tvPantalla: TextView
    private lateinit var scrollPantalla: HorizontalScrollView // <--- Variable para el scroll

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Inicializar la pantalla y el scroll
        tvPantalla = findViewById(R.id.textView)
        scrollPantalla = findViewById(R.id.scrollPantalla) // <--- Asegúrate que este ID esté en tu XML

        // 2. Configurar botones numéricos y punto
        val botonesId = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnPunto
        )

        botonesId.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                val boton = it as Button
                val textoActual = tvPantalla.text.toString()

                // Limite de 50 caracteres para que sea "largo" pero seguro
                if (textoActual.length < 50) {
                    if (textoActual == "0" || textoActual == "Error" || textoActual.contains("Gabriel")) {
                        tvPantalla.text = boton.text
                    } else {
                        tvPantalla.append(boton.text)
                    }
                    desplazarAlFinal() // <--- Mueve la vista al escribir
                }
            }
        }

        // 3. Configurar botones de operaciones
        val operaciones = mapOf(
            R.id.btnSuma to "+",
            R.id.btnResta to "-",
            R.id.btnMultiplicacion to "x",
            R.id.btnDivision to "÷",
            R.id.btnPorcentaje to "%"
        )

        operaciones.forEach { (id, simbolo) ->
            findViewById<Button>(id).setOnClickListener {
                tvPantalla.append(simbolo)
                desplazarAlFinal()
            }
        }

        // 4. Botón Reset (C)
        findViewById<Button>(R.id.btnReset).setOnClickListener {
            tvPantalla.text = "0"
            desplazarAlFinal()
        }

        // 5. Botón Borrar (Retroceso)
        findViewById<Button>(R.id.btnBorrar).setOnClickListener {
            val texto = tvPantalla.text.toString()
            if (texto.isNotEmpty() && texto != "0") {
                tvPantalla.text = texto.dropLast(1)
                if (tvPantalla.text.isEmpty()) tvPantalla.text = "0"
            }
            desplazarAlFinal()
        }

        // 6. Botón Más/Menos (±)
        findViewById<Button>(R.id.btnMasMenos).setOnClickListener {
            val texto = tvPantalla.text.toString()
            if (texto.isNotEmpty() && texto != "Error" && texto != "0") {
                if (texto.startsWith("-")) {
                    tvPantalla.text = texto.substring(1)
                } else {
                    tvPantalla.text = "-$texto"
                }
            }
        }

        // 7. BOTÓN IGUAL (=)
        findViewById<Button>(R.id.btnIgual).setOnClickListener {
            val resultado = calcularResultado(tvPantalla.text.toString())
            tvPantalla.text = resultado
            desplazarAlFinal()
        }
    }

    // Función para que el scroll siempre siga al último número escrito
    private fun desplazarAlFinal() {
        scrollPantalla.post {
            scrollPantalla.fullScroll(android.view.View.FOCUS_RIGHT)
        }
    }

    // Lógica de cálculo con exp4j
    fun calcularResultado(operacion: String): String {
        if (operacion.isEmpty() || operacion.contains("Gabriel") || operacion == "Error") return "0"

        return try {
            val textoLimpio = operacion.replace("x", "*")
                .replace("÷", "/")
                .replace("%", "/100")

            val expresion = ExpressionBuilder(textoLimpio).build()
            val resultado = expresion.evaluate()

            if (resultado % 1 == 0.0) {
                resultado.toLong().toString()
            } else {
                resultado.toString()
            }
        } catch (e: Exception) {
            "Error"
        }
    }
}