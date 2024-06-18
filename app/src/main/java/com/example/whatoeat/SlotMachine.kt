package com.example.whatoeat

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import com.example.whatoeat.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random


class slotMachine : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slot_machine)

        val list_title = intent.getStringExtra("list_title")
        val choice = intent.getStringArrayListExtra("list")

        val title: TextView = findViewById(R.id.List_title)
        val list: ListView = findViewById(R.id.choice_list)
        val chooseButton: Button = findViewById(R.id.choose_button)
        val finalChoice: TextView = findViewById(R.id.final_choice)

        // Convert the ArrayList<String> to a List<String>
        val choiceList: List<String> = choice?.toList() ?: listOf()

        val adapter = MyAdapter(this, choiceList)

        title.text = list_title
        list.adapter = adapter

        chooseButton.setOnClickListener {
            val randomTimePicker = Random.nextInt(20, 40)
            var index = 0
            var timeCount = 0
            var delayTime: Long = 30

            val coroutineScope = CoroutineScope(Dispatchers.Main)

            finalChoice.text = ""
            finalChoice.setBackgroundResource(R.color.background)
            coroutineScope.launch {
                while (timeCount < randomTimePicker) {
                    if (index == choiceList.size) {
                        index = 0
                    }
                    adapter.setEnabledPosition(index)
                    index++
                    timeCount++
                    delayTime += 5
                    delay(delayTime)
                }
                // Use the correct index after the loop
                val finalIndex = if (index == 0) choiceList.size - 1 else index - 1
                finalChoice.text = "${choiceList[finalIndex]}"
                finalChoice.setBackgroundResource(R.color.button)
                chooseButton.text = "Choose Again"

            }

        }






    }
}