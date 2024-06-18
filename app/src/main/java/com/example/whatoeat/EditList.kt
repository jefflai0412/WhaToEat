package com.example.whatoeat

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class EditList : AppCompatActivity() {

    private lateinit var dbManager: TreeDatabaseManager
    private lateinit var adapter: ArrayAdapter<String>
    private val childNodes = mutableListOf<TreeNode>()
    private var parentNodeId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_list)

        dbManager = TreeDatabaseManager(this)
        dbManager.open()

        val listTitle = intent.getStringExtra("list_title")
        val parentNodeId = intent.getLongExtra("parent_node_id", 0)
        this.parentNodeId = parentNodeId
        val title: TextView = findViewById(R.id.title)
        title.text = listTitle

        val editList: ListView = findViewById(R.id.edit_list)
        val nextButton: Button = findViewById(R.id.next_button)
        val addButton: Button = findViewById(R.id.add_button)

        var list = ArrayList(childNodes.map { it.name })

        adapter = MyAdapter(this, list)
        editList.adapter = adapter

        loadChildNodesFromDatabase(parentNodeId)

        addButton.setOnClickListener {
            showCreateListDialog()
        }

        editList.setOnItemClickListener { _, _, position, _ ->
            list.removeAt(position)
            adapter.notifyDataSetChanged()
        }

        nextButton.setOnClickListener {
            val intent = Intent(this, slotMachine::class.java).apply {
                putExtra("list_title", listTitle)
                putStringArrayListExtra("list", list)
            }
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        dbManager.close()
        super.onDestroy()
    }

    private fun loadChildNodesFromDatabase(parentId: Long) {
        childNodes.clear()
        val nodes = dbManager.getNodesByParentId(parentId)
        childNodes.addAll(nodes)
        adapter.clear()
        adapter.addAll(childNodes.map { it.name })
        adapter.notifyDataSetChanged()
    }

    private fun showCreateListDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Child Node")

        val view = layoutInflater.inflate(R.layout.dialog_create_list, null)
        builder.setView(view)

        val input = view.findViewById<EditText>(R.id.edit_text_list_name)

        builder.setPositiveButton("Create") { dialog, _ ->
            val childName = input.text.toString()
            if (childName.isNotEmpty()) {
                dbManager.addChildNode(childName, parentNodeId)
                loadChildNodesFromDatabase(parentNodeId)
                Toast.makeText(this, "Item '$childName' created", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }
}