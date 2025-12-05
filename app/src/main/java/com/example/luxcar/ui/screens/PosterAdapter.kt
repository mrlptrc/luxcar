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
    private var images: MutableMap<Long, ByteArray?>,
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
        val car = cars.find { it.id == poster.carId }
        val context = holder.itemView.context

        holder.title.text = poster.titulo
        holder.subtitle.text = "${car?.marca} ${car?.modelo} (${car?.ano})"
        holder.price.text = context.getString(R.string.price, poster.preco)
        holder.btnEdit.text = context.getString(R.string.edit_ad)
        holder.btnDelete.text = context.getString(R.string.delete)

        holder.itemView.tag = "poster_item_${poster.titulo}"
        holder.itemView.contentDescription = "poster_item_${poster.titulo}"

        holder.title.tag = "poster_title_${poster.titulo}"
        holder.price.tag = "poster_price_${poster.titulo}"
        holder.price.contentDescription = poster.preco.toString()

        holder.btnEdit.tag = "edit_post_button_${poster.titulo}"
        holder.btnEdit.contentDescription = "edit_post_button_${poster.titulo}"

        holder.btnDelete.tag = "delete_post_button_${poster.titulo}"
        holder.btnDelete.contentDescription = "delete_post_button_${poster.titulo}"

        // flag de negociação
        if(poster.emNegociacao) {
            holder.flagNegociacao.visibility = View.VISIBLE
        } else {
            holder.flagNegociacao.visibility = View.GONE
        }

        // imagem
        images[poster.id]?.let { imgBytes ->
            BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.size)?.let {
                holder.img.setImageBitmap(it)
                holder.img.contentDescription = "poster_image_${poster.titulo}"
            }
        }

        // ações
        holder.itemView.setOnClickListener { onOpen(poster) }
        holder.btnEdit.setOnClickListener { onEdit(poster) }
        holder.btnDelete.setOnClickListener { onDelete(poster) }
    }

    fun updateList(
        newList: List<Poster>,
        newImages: Map<Long, ByteArray?>,
        newCars: List<Car>
    ) {
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