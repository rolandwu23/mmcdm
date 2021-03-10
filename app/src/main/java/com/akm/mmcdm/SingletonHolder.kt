package com.akm.mmcdm

open class SingletonHolder<out T: Any, in A, in B>(creator: (A,B) -> T) {
    private var creator: ((A,B) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(argA: A,argB: B): T {
        val checkInstance = instance
        if (checkInstance != null) {
            return checkInstance
        }

        return synchronized(this) {
            val checkInstanceAgain = instance
            if (checkInstanceAgain != null) {
                checkInstanceAgain
            } else {
                val created = creator!!(argA,argB)
                instance = created
                creator = null
                created
            }
        }
    }
}