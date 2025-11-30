package com.example.luxcar.ui.screens

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.luxcar.R
import com.example.luxcar.data.model.Car
import com.example.luxcar.data.model.Poster

class PosterAdapter(
    private var posters: List<Poster>,
    private val cars: MutableList<Car>,
    private var images: MutableMap<Int, ByteArray?>,
    private val onOpen: (Poster) -> Unit,
    private val onEdit: (Poster) -> Unit,
    private val onDelete: (Poster) -> Unit
) : RecyclerView.Adapter<PosterAdapter.PosterViewHolder>() {

    inner class PosterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.posterImage)
        val title: TextView = itemView.findViewById(R.id.posterTitle)
        val subtitle: TextView = itemView.findViewById(R.id.posterSubtitle)
        val price: TextView = itemView.findViewById(R.id.posterPrice)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
        val flagNegociacao: TextView = itemView.findViewById(R.id.flagNegociacao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_poster, parent, false)
        return PosterViewHolder(view)
    }

    override fun getItemCount(): Int = posters.size

    override fun onBindViewHolder(holder: PosterViewHolder, position: Int) {
        val poster = posters[position]
        val car = cars.find { it.id.toLong() == poster.carId }
        val context = holder.itemView.context // pega context para tradução

        holder.title.text = poster.titulo
        holder.subtitle.text = "${car?.marca} ${car?.modelo} (${car?.ano})"

        // usa string resource para formatação de preço
        holder.price.text = context.getString(R.string.price, poster.preco)

        // traduz textos dos botões
        holder.btnEdit.text = context.getString(R.string.edit_ad)
        holder.btnDelete.text = context.getString(R.string.ad_deleted)

        if(poster.emNegociacao) {
            holder.flagNegociacao.visibility = View.VISIBLE
        } else {
            holder.flagNegociacao.visibility = View.GONE
        }

        // imagem
        images[poster.id]?.let { imgBytes ->
            BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.size)?.let {
                holder.img.setImageBitmap(it)
            }
        }

        // ações
        holder.itemView.setOnClickListener { onOpen(poster) }
        holder.btnEdit.setOnClickListener { onEdit(poster) }
        holder.btnDelete.setOnClickListener { onDelete(poster) }
    }

    fun updateList(newList: List<Poster>, newImages: Map<Int, ByteArray?>, newCars: List<Car>) {
        this.posters = newList
        this.images.clear()
        this.images.putAll(newImages)
        (this.cars as MutableList).apply {
            clear()
            addAll(newCars)
        }
        notifyDataSetChanged()
    }
}