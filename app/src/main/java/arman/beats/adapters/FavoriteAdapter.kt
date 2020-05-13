package arman.beats.adapters

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import arman.beats.R
import arman.beats.Songs
import arman.beats.fragments.SongPlayingFragment

class FavoriteAdapter(_songDetails: ArrayList<Songs>, _context: Context)
    : RecyclerView.Adapter<FavoriteAdapter.FavContentViewHolder>(){

    var songDetails:ArrayList<Songs>?=null
    var mContext: Context?=null
    var mediaplayer:MediaPlayer?=null


    init {
        this.songDetails=_songDetails
        this.mContext=_context
        this.mediaplayer=SongPlayingFragment.Statified.mediaPlayer
    }

    override fun onBindViewHolder(holder: FavContentViewHolder, position: Int) {
        val songObject=  songDetails?.get(position)
        if (songObject?.artist.equals("<unknown>", ignoreCase = true)) {
            holder.trackArtist?.setText("unknown")
        } else {
            holder.trackArtist?.setText(songObject?.artist)
        }
        holder.trackTitle?.setText(songObject?.songTitle)
        holder.contentHolder?.setOnClickListener(View.OnClickListener {
            try {
                if (mediaplayer?.isPlaying() as Boolean) {
                    mediaplayer?.stop()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val songPlayingFragment= SongPlayingFragment()
            val args= Bundle()
            args.putString("songArtist",songObject?.artist)
            args.putString("Path",songObject?.songData)
            args.putString("songTitle",songObject?.songTitle)
            args.putInt("songId",songObject?.songID?.toInt() as Int)
            args.putInt("songPosition",position)
            args.putParcelableArrayList("songData",songDetails)

            songPlayingFragment.arguments=args

            (mContext as FragmentActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.details_fragment, songPlayingFragment)
                .addToBackStack("SongPlayingFragment")
                .commit()
        })

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavContentViewHolder {
        val itemView= LayoutInflater.from(parent?.context)
            .inflate(R.layout.favorite_content_custom_row, parent, false)
        return FavContentViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        if (songDetails==null){
            return 0
        }else{
            return (songDetails?.size as Int)
        }

    }

   inner class FavContentViewHolder(view: View): RecyclerView.ViewHolder(view){
        var trackTitle: TextView?=null
        var trackArtist: TextView?=null
        var contentHolder: RelativeLayout?=null
        init {
            trackTitle= view.findViewById<TextView>(R.id.trackTitle)
            trackArtist=view.findViewById<TextView>(R.id.trackArtist)
            contentHolder=view.findViewById<RelativeLayout>(R.id.contentRow)
        }

    }
}