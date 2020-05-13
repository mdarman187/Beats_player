package arman.beats.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import arman.beats.R
import arman.beats.Songs
import arman.beats.activities.MainActivity
import arman.beats.fragments.MainScreenFragment
import arman.beats.fragments.SongPlayingFragment

class MainScreenAdapter(_songDetails: ArrayList<Songs>, _context: Context)
    : RecyclerView.Adapter<MainScreenAdapter.MyViewHolder>(){

    var songDetails:ArrayList<Songs>?=null
    var mContext:Context?=null

    init {
        this.songDetails=_songDetails
        this.mContext=_context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val songObject=  songDetails?.get(position)
        if (songObject?.artist.equals("<unknown>", ignoreCase = true)) {
            holder.trackArtist?.text = "unknown"
        } else {
            holder.trackArtist?.text = songObject?.artist
        }

        holder.trackTitle?.text = songObject?.songTitle
        holder.contentHolder?.setOnClickListener({
            try {
                if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                    SongPlayingFragment.Statified.mediaPlayer?.stop()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView= LayoutInflater.from(parent?.context)
            .inflate(R.layout.row_custom_mainscreen_adapter, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        if (songDetails==null){
            return 0
        }else{
            return (songDetails as ArrayList<Songs>).size
        }

    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        var trackTitle:TextView?=null
        var trackArtist:TextView?=null
        var contentHolder:RelativeLayout?=null
        init {
            trackTitle= view.findViewById<TextView>(R.id.trackTitle)
            trackArtist=view.findViewById<TextView>(R.id.trackArtist)
            contentHolder=view.findViewById<RelativeLayout>(R.id.contentRow)
        }

    }
}