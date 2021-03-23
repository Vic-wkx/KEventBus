package com.example.eventbus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.eventbus.databinding.ActivityMainBinding
import com.library.keventbus.KEventBus
import com.library.keventbus.Subscribe

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnRegister.setOnClickListener {
            KEventBus.register(this)
        }
        binding.btnUnregister.setOnClickListener {
            KEventBus.unregister(this)
        }
        binding.btnSend.setOnClickListener {
            KEventBus.post("I'm a string message.")
        }
        binding.btnThread.setOnClickListener {
            Toast.makeText(this, "Not supported yet.", Toast.LENGTH_SHORT).show()
        }
        binding.btnSticky.setOnClickListener {
            Toast.makeText(this, "Not supported yet.", Toast.LENGTH_SHORT).show()
        }
        binding.btnClear.setOnClickListener {
            binding.tvResult.text = "Result:"
        }
    }

    @Subscribe
    fun onStringEvent(message: String) {
        binding.tvResult.append("\n$message")
    }
}