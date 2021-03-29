package com.example.eventbus

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.eventbus.databinding.ActivityMainBinding
import com.library.keventbus.KEventBus
import com.library.keventbus.Subscribe
import com.library.keventbus.ThreadMode

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
            KEventBus.post(AsyncEvent("It's an async message."))
        }
        binding.btnSticky.setOnClickListener {
            KEventBus.postSticky(StickyEvent("It's a sticky message"))
        }
        binding.btnRemoveSticky.setOnClickListener {
            KEventBus.removeSticky(StickyEvent::class.java)
        }
        binding.btnClear.setOnClickListener {
            binding.tvResult.text = "Result:"
        }
    }

    @Subscribe
    fun onStringEvent(message: String) {
        binding.tvResult.append("\n$message")
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onAsyncEvent(event: AsyncEvent) {
        Log.d("~~~", "${event.content} ${Thread.currentThread().name}")
    }

    @Subscribe(sticky = true)
    fun onStickyEvent(event: StickyEvent) {
        binding.tvResult.append("\n${event.content}")
    }
}