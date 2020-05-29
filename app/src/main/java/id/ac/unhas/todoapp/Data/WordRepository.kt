package id.ac.unhas.todoapp.Data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class WordRepository(private val wordDao: WordDao){
    val allWords: LiveData<List<Word>> = wordDao.getAllWords()

    @WorkerThread
    fun insert(word: Word) {
        wordDao.insert(word)
    }

    @WorkerThread
    fun delete(word: Word) {
        wordDao.deleteTask(word)
    }

}