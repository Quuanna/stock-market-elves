package com.mitake.finacialidea.viewpagercards

import androidx.cardview.widget.CardView

interface CardAdapter {
    val baseElevation: Float
    fun getCardViewAt(position: Int): CardView?
    val count: Int

    companion object {
        const val MAX_ELEVATION_FACTOR = 8
    }
}