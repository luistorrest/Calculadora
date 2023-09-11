package com.luistorrest.calculadora_p1

import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    var Resultado_TextView:TextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Resultado_TextView=findViewById(R.id.Resultado_TextView)
    }
    fun calcular(view : View) {

        var press_button = view as Button
        var text_button = press_button.text.toString()
        var concatenar_textView = Resultado_TextView?.text.toString()+text_button
        var concatenarSinCeros = quitarCerosIzquirda(concatenar_textView)

        if(text_button=="=") {
            var resultado=0.0
            resultado = evaluarExpresionMatematicaSimple(Resultado_TextView?.text.toString())
            Resultado_TextView?.text=resultado.toString()
        }else if(text_button=="Delete"){
            Resultado_TextView?.text="0"
        }else{
            Resultado_TextView?.text = concatenarSinCeros
        }
    }
    fun quitarCerosIzquirda(str : String):String{
            var i=0
            while (i<str.length && str[i]=='0')i++
            val sb=StringBuffer(str)
            sb.replace(0,i,"")
            return sb.toString()
        }

    fun evaluarExpresionMatematicaSimple(expresion: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            /**
             * Avanza al siguiente carácter en la expresión de entrada.
             */
            fun siguienteCaracter() {
                ch = if (++pos < expresion.length) expresion[pos].toInt() else -1
            }

            /**
             * Consume caracteres de espacio en blanco y avanza al siguiente carácter que no sea espacio en blanco.
             *
             * @param charToEat El carácter que se debe consumir.
             * @return True si se consume el carácter especificado, false en caso contrario.
             */
            fun consumir(charToEat: Int): Boolean {
                while (ch == ' '.toInt()) siguienteCaracter()
                if (ch == charToEat) {
                    siguienteCaracter()
                    return true
                }
                return false
            }

            /**
             * Analiza la expresión matemática y devuelve el resultado.
             *
             * @return El resultado de evaluar la expresión como un Double.
             * @throws RuntimeException Si hay caracteres inesperados en la expresión.
             */
            fun analizar(): Double {
                siguienteCaracter()
                val x = analizarExpresion()
                if (pos < expresion.length) throw RuntimeException("Inesperado: " + ch.toChar())
                return x
            }

            // Resto del código para analizar expresiones, términos y factores
            // (solo suma, resta, multiplicación y división)

            fun analizarExpresion(): Double {
                var x = analizarTermino()
                while (true) {
                    if (consumir('+'.toInt())) x += analizarTermino() // suma
                    else if (consumir('-'.toInt())) x -= analizarTermino() // resta
                    else return x
                }
            }

            fun analizarTermino(): Double {
                var x = analizarFactor()
                while (true) {
                    if (consumir('*'.toInt())) x *= analizarFactor() // multiplicación
                    else if (consumir('/'.toInt())) x /= analizarFactor() // división
                    else return x
                }
            }

            fun analizarFactor(): Double {
                if (consumir('('.toInt())) {
                    val x = analizarExpresion()
                    consumir(')'.toInt())
                    return x
                } else {
                    val startPos = pos
                    while (ch >= '0'.toInt() && ch <= '9'.toInt() || ch == '.'.toInt()) {
                        siguienteCaracter()
                    }
                    return expresion.substring(startPos, pos).toDouble()
                }
            }

        }.analizar()
    }

}