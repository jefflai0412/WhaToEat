package com.example.whatoeat

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getLongOrNull

class TreeDatabaseManager(context: Context) {
    private val dbHelper = TreeDatabaseHelper(context)
    private var database: SQLiteDatabase? = null

    fun open() {
        database = dbHelper.writableDatabase
    }

    fun close() {
        dbHelper.close()
    }

    fun addParentNode(parentName: String): Long {
        val values = ContentValues().apply {
            put(TreeDatabaseHelper.COLUMN_NAME, parentName)
            put(TreeDatabaseHelper.COLUMN_PARENT_ID, null as Int?) // Explicitly cast null to Integer?
        }
        return database!!.insert(TreeDatabaseHelper.TABLE_TREES, null, values)
    }

    fun addChildNode(childName: String, parentId: Long): Long {
        val values = ContentValues().apply {
            put(TreeDatabaseHelper.COLUMN_NAME, childName)
            put(TreeDatabaseHelper.COLUMN_PARENT_ID, parentId)
        }
        return database!!.insert(TreeDatabaseHelper.TABLE_TREES, null, values)
    }

    fun getNodesByParentId(parentId: Long?): List<TreeNode> {
        val nodeList = mutableListOf<TreeNode>()
        val selection = if (parentId == null) "${TreeDatabaseHelper.COLUMN_PARENT_ID} IS NULL" else "${TreeDatabaseHelper.COLUMN_PARENT_ID} = ?"
        val selectionArgs = parentId?.let { arrayOf(it.toString()) } ?: null

        val cursor: Cursor = database!!.query(TreeDatabaseHelper.TABLE_TREES,
            null, selection, selectionArgs, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val node = TreeNode(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(TreeDatabaseHelper.COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(TreeDatabaseHelper.COLUMN_NAME)),
                    parentId = cursor.getLongOrNull(cursor.getColumnIndexOrThrow(TreeDatabaseHelper.COLUMN_PARENT_ID))
                )
                nodeList.add(node)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return nodeList
    }
}
