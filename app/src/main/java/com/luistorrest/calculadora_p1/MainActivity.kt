package com.luistorrest.calculadora_p1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    // Declaración de la vista de resultado
    var resultadoTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialización de la vista de resultado
        resultadoTextView = findViewById(R.id.Resultado_TextView)
    }

    fun calcular(view: View) {

        // Obtiene el botón presionado
        val botonPresionado = view as Button
        val textoBoton = botonPresionado.text.toString()

        // Concatena el texto del botón con el texto actual en la vista de resultado
        val textoConcatenado = resultadoTextView?.text.toString() + textoBoton

        // Elimina los ceros a la izquierda en la cadena concatenada
        val textoSinCeros = quitarCerosIzquierda(textoConcatenado)

        if (textoBoton == "=") {
            // Evalúa la expresión matemática y muestra el resultado
            val resultado = evaluarExpresionMatematicaSimple(resultadoTextView?.text.toString())
            resultadoTextView?.text = resultado.toString()
        } else if (textoBoton == "Delete") {
            // Borra el contenido de la vista de resultado
            resultadoTextView?.text = "0"
        } else {
            // Muestra la cadena sin ceros en la vista de resultado
            resultadoTextView?.text = textoSinCeros
        }
    }

    // Elimina ceros a la izquierda de la cadena
    fun quitarCerosIzquierda(str: String): String {
        var i = 0
        while (i < str.length && str[i] == '0') i++
        val sb = StringBuffer(str)
        sb.replace(0, i, "")
        return sb.toString()
    }

    // Función para evaluar expresiones matemáticas simples
    fun evaluarExpresionMatematicaSimple(expresion: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            // Avanza al siguiente carácter en la expresión de entrada
            fun siguienteCaracter() {
                ch = if (++pos < expresion.length) expresion[pos].code else -1
            }

            // Consume caracteres de espacio en blanco y avanza al siguiente carácter que no sea espacio en blanco
            fun consumir(charToEat: Int): Boolean {
                while (ch == ' '.code) siguienteCaracter()
                if (ch == charToEat) {
                    siguienteCaracter()
                    return true
                }
                return false
            }

            // Analiza la expresión matemática y devuelve el resultado
            fun analizar(): Double {
                siguienteCaracter()
                val x = analizarExpresion()
                if (pos < expresion.length) throw RuntimeException("Inesperado: " + ch.toChar())
                return x
            }

            // Resto del código para analizar expresiones, términos y factores (solo suma, resta, multiplicación y división)
            fun analizarExpresion(): Double {
                var x = analizarTermino()
                while (true) {
                    if (consumir('+'.code)) x += analizarTermino() // suma
                    else if (consumir('-'.code)) x -= analizarTermino() // resta
                    else return x
                }
            }

            fun analizarTermino(): Double {
                var x = analizarFactor()
                while (true) {
                    if (consumir('*'.code)) x *= analizarFactor() // multiplicación
                    else if (consumir('/'.code)) x /= analizarFactor() // división
                    else return x
                }
            }

            fun analizarFactor(): Double {
                if (consumir('('.code)) {
                    val x = analizarExpresion()
                    consumir(')'.code)
                    return x
                } else {
                    val startPos = pos
                    while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) {
                        siguienteCaracter()
                    }
                    return expresion.substring(startPos, pos).toDouble()
                }
            }

        }.analizar()
    }
}
