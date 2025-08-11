// ================================================
// Patterns:
// Language: Kotlin (Android)
// Purpose:
// ================================================
package com.cheq.sastandroid


import android.app.Activity
import android.os.Bundle
import android.webkit.WebView
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.IOException
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.net.URL
import java.io.ObjectInputStream
import java.io.ByteArrayInputStream
import android.util.Base64
import org.json.JSONObject
import java.security.SecureRandom
import android.content.Intent
import android.net.Uri
import android.webkit.JavascriptInterface
import java.io.FileInputStream
import java.io.File


class DemoActivity : Activity() {   
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup demo DB helper
        val dbHelper = object : SQLiteOpenHelper(this, "demo.db", null, 1) {
            override fun onCreate(db: SQLiteDatabase) {}
            override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
        }
        val db = dbHelper.readableDatabase
        val webView = WebView(this)
        val userInput = "" // placeholder

        // Invoke all
        vulnerableSql(db, userInput)
        safeSql(db, userInput)
        xss(webView, userInput)
        safeXss(webView, userInput)
        cmdInjection(userInput)
        safeCmd(userInput)
        emptyCatch()
        safeCatch()
        magicNumber()
        namedConstant()
        nestedConditions(true, true, true)
        flatConditions(true, true, true)
        unusedVar()
        usedVar()
    }

    // ---------------------------------------------------
    // ⚠️
    // ---------------------------------------------------
    fun vulnerableSql(db: SQLiteDatabase, userInput: String) {
        val query = "SELECT * FROM users WHERE id = '$userInput'"
        db.rawQuery(query, null)
    }

    // ---------------------------------------------------
    // ✅
    // ---------------------------------------------------
    fun safeSql(db: SQLiteDatabase, userInput: String) {
        val query = "SELECT * FROM users WHERE id = ?"
        db.rawQuery(query, arrayOf(userInput))
    }

    // ---------------------------------------------------
    // ⚠️
    // ---------------------------------------------------
    fun xss(webView: WebView, userInput: String) {
        webView.loadData("<html><body>$userInput</body></html>", "text/html", "UTF-8")
    }

    // ---------------------------------------------------
    // ✅
    // ---------------------------------------------------
    fun safeXss(webView: WebView, userInput: String) {
        webView.loadData(
            "<html><body>${android.text.Html.escapeHtml(userInput)}</body></html>",
            "text/html",
            "UTF-8"
        )
    }

    // ---------------------------------------------------
    // ⚠️
    // ---------------------------------------------------
    fun cmdInjection(userInput: String) {
        Runtime.getRuntime().exec("ls $userInput")
    }

    // ---------------------------------------------------
    // ✅
    // ---------------------------------------------------
    fun safeCmd(userInput: String) {
        Runtime.getRuntime().exec(arrayOf("ls", userInput))
    }

    // ---------------------------------------------------
    // ⚠️
    // ---------------------------------------------------
    fun emptyCatch() {
        try {
            throw IOException("test")
        } catch (_: IOException) {
            // Empty catch hides errors
        }
    }

    // ---------------------------------------------------
    // ✅
    // ---------------------------------------------------
    fun safeCatch() {
        try {
            throw IOException("test")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // ---------------------------------------------------
    // ⚠️
    // ---------------------------------------------------
    fun magicNumber() {
        for (i in 0 until 5) { /* ⚠️ “5” is a magic number */ }
    }

    companion object {
        // ---------------------------------------------------
        // ✅
        // ---------------------------------------------------
        const val MAX_RETRIES = 5
    }
    fun namedConstant() {
        for (i in 0 until MAX_RETRIES) { }
    }

    // ---------------------------------------------------
    // ⚠️
    // ---------------------------------------------------
    fun nestedConditions(a: Boolean, b: Boolean, c: Boolean) {
        if (a) {
            if (b) {
                if (c) {
                    println("All true")
                    return
                }
            }
        }
        println("Not all true")
    }

    // ---------------------------------------------------
    // ✅
    // ---------------------------------------------------
    fun flatConditions(a: Boolean, b: Boolean, c: Boolean) {
        if (!a || !b || !c) {
            println("Not all true")
            return
        }
        println("All true")
    }

    // ---------------------------------------------------
    // ⚠️
    // ---------------------------------------------------
    fun unusedVar() {
        val unused = 42  // ⚠️ never used
    }

    // ---------------------------------------------------
    // ✅
    // ---------------------------------------------------
    fun usedVar() {
        val used = 42
        println(used)
    }

    // ---------------------------------------------------
    // CSA Test : Weak Cryptography 
    // ⚠️
    // ---------------------------------------------------
    fun weakCrypto(password: String): String {
        val md = MessageDigest.getInstance("MD5")  // Weak algorithm
        val hash = md.digest(password.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }  
    }

}
