package com.uxo.monax.util

import android.app.Activity
import com.google.android.gms.games.GamesSignInClient
import com.google.android.gms.games.LeaderboardsClient
import com.google.android.gms.games.PlayGames

class PlayGamesHelper(private val activity: Activity) {

    companion object {
        private val LEADERBOARD_ID = "CgkIkLnT8ZYDEAIQAQ"
    }

    private val gamesSignInClient: GamesSignInClient   = PlayGames.getGamesSignInClient(activity)
    private val leaderboardsClient: LeaderboardsClient = PlayGames.getLeaderboardsClient(activity)

    /** Перевіряє, чи гравець увійшов у Play Games */
    fun isAuthenticated(block: (Boolean) -> Unit) {
        gamesSignInClient.isAuthenticated().addOnCompleteListener { task ->
            val isSignedIn = task.isSuccessful && task.result.isAuthenticated
            block(isSignedIn)
        }
    }

    /** Примусовий вхід у Play Games */
    fun signIn(block: (() -> Unit)? = null) {
        gamesSignInClient.signIn().addOnCompleteListener { task ->
            if (task.isSuccessful && task.result.isAuthenticated) {
                block?.invoke() // ✅ Виконати, якщо вхід успішний
            } else {
                log("Play Games: Вхід не вдався: ${task.exception?.message}")
            }
        }
    }

    /** Отримує ім'я гравця (наприклад, для відображення в UI) */
    fun getPlayerName(block: (String?) -> Unit) {
        isAuthenticated { isSignedIn ->
            if (isSignedIn) {
                PlayGames.getPlayersClient(activity)
                    .currentPlayer
                    .addOnSuccessListener { player -> block(player.displayName) }
                    .addOnFailureListener { exception ->
                        log("Play Games: Не вдалося отримати ім'я гравця: ${exception.message}")
                        block(null)
                    }
            } else {
                log("Play Games: Гравець не авторизований")
                block(null)
            }
        }
    }

    /** Відправляє очки у Leaderboard */
    fun submitScore(score: Long) {
        isAuthenticated { isSignedIn ->
            if (isSignedIn) {
                leaderboardsClient.submitScore(LEADERBOARD_ID, score)
                log("Play Games: Очки ($score) відправлено в Leaderboard")
            } else {
                log("Play Games: Гравець не авторизований, не можна відправити очки")
            }
        }
    }

    /** Відкриває Leaderboard */
    fun showLeaderboard() {
        isAuthenticated { isSignedIn ->
            if (isSignedIn) {
                leaderboardsClient.getLeaderboardIntent(LEADERBOARD_ID)
                    .addOnSuccessListener { intent -> activity.startActivityForResult(intent, 9004) }
                    .addOnFailureListener { exception ->
                        log("Play Games: Не вдалося відкрити Leaderboard: ${exception.message}")
                    }
            } else {
                log("Play Games: Гравець не авторизований, пропонуємо вхід...")
                signIn { showLeaderboard() } // 🔄 Автоматичний вхід і повторна спроба відкриття Leaderboard
            }
        }
    }
}
