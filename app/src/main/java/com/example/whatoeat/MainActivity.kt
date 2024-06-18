package com.example.whatoeat

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    private lateinit var dbManager: TreeDatabaseManager
    private lateinit var adapter: ArrayAdapter<String>
    private val parentNodes = mutableListOf<TreeNode>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbManager = TreeDatabaseManager(this)
        dbManager.open()

        val listView: ListView = findViewById(R.id.top_list)
        val addButton: Button = findViewById(R.id.add_button)

        adapter = MyAdapter(this, parentNodes.map { it.name })
        listView.adapter = adapter

        loadParentNodesFromDatabase()

        addButton.setOnClickListener {
            showCreateListDialog()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedNode = parentNodes[position]
            val intent = Intent(this, EditList::class.java).apply {
                putExtra("list_title", selectedNode.name)
                putExtra("parent_node_id", selectedNode.id)
            }
            startActivity(intent)
        }

    }

    override fun onDestroy() {
        dbManager.close()
        super.onDestroy()
    }

    private fun loadParentNodesFromDatabase() {
        parentNodes.clear()
        val rootNodes = dbManager.getNodesByParentId(null)
        parentNodes.addAll(rootNodes)
        adapter.clear()
        adapter.addAll(parentNodes.map { it.name })
        adapter.notifyDataSetChanged()
    }

    private fun showCreateListDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Create New List")

        val input = EditText(this)
        dialog.setView(input)

        dialog.setPositiveButton("OK") { _, _ ->
            val nodeName = input.text.toString()
            if (nodeName.isNotBlank()) {
                dbManager.addParentNode(nodeName)
                loadParentNodesFromDatabase()
            }
        }
        dialog.setNegativeButton("Cancel", null)
        dialog.show()
    }
}
