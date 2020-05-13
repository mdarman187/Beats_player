package arman.beats.fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arman.beats.CurrentSongHelper
import arman.beats.Databases.BeatsDatabase
import arman.beats.R
import arman.beats.Songs
import arman.beats.adapters.FavoriteAdapter

class FavoriteFragment : Fragment() {

    var myActivity:Activity?=null
    var noFavorites:TextView?=null
    var nowPlayingBottomBar:RelativeLayout?=null
    var playPauseButton:ImageButton?=null
    var songTitle:TextView?=null
    var recyclerView:RecyclerView?=null
    var favoriteContent:BeatsDatabase?=null
    var refreshList:ArrayList<Songs>?=null
    var getListfromDatabase: ArrayList<Songs>?=null
    var playPauseHelper = CurrentSongHelper()

    object Statified{
        var mediaPlayer:MediaPlayer?=null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_favorite, container, false)
        activity?.title="Favorites"
        favoriteContent= BeatsDatabase(activity)
        playPauseHelper.isPlaying = false
        noFavorites=view?.findViewById(R.id.noFavorites)
        nowPlayingBottomBar=view?.findViewById(R.id.hiddenBarFavScreen)
        playPauseButton=view?.findViewById(R.id.playPauseButton)
        songTitle=view?.findViewById(R.id.songTitleFavScreen)
        recyclerView=view?.findViewById(R.id.favoriteRecycler)
        (nowPlayingBottomBar as RelativeLayout).isClickable = false

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        myActivity= context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)

        myActivity=activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        display_favorites_by_searching()
        bottomBarSetup()

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }

    fun getSongsFromPhone():ArrayList<Songs>?{
        val arrayList= ArrayList<Songs>()
        val contentResolver= activity?.contentResolver
        val songUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor= contentResolver?.query(songUri, null,null,null,null)
        if (songCursor!= null && songCursor.moveToFirst()){
            val songId= songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle= songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist= songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData= songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)

            do{
                val currentId= songCursor.getLong(songId)
                val currentTitle= songCursor.getString(songTitle)
                val  currentArtist= songCursor.getString(songArtist)
                val currentData= songCursor.getString(songData)
                val currentDate= songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)
                arrayList.add(Songs(currentId,currentTitle,currentArtist,currentData,currentDate.toLong()))
            }while (songCursor.moveToNext())
        }else{
            return null
        }
        songCursor.close()
        return arrayList
    }

    fun bottomBarSetup(){
        try {
            nowPlayingBottomBar?.isClickable = false
            bottomBarClickHandler()
            SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener({
                songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
                SongPlayingFragment.Staticated.onSongComplete()
            })
            songTitle?.text =SongPlayingFragment.Statified.currentSongHelper?.songTitle
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean){
                playPauseHelper.isPlaying = true
                nowPlayingBottomBar?.visibility= View.VISIBLE
            }else{
                playPauseHelper.isPlaying = false
                nowPlayingBottomBar?.visibility= View.INVISIBLE
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun bottomBarClickHandler(){
        nowPlayingBottomBar?.setOnClickListener({
            Statified.mediaPlayer=SongPlayingFragment.Statified.mediaPlayer
            val songPlayingFragment= SongPlayingFragment()
            val args= Bundle()
            args.putString("songArtist",SongPlayingFragment.Statified.currentSongHelper?.songArtist)
            args.putString("Path",SongPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putString("songTitle",SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            args.putInt("songId",SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt("songPosition",SongPlayingFragment.Statified.currentSongHelper?.currentPosition?.toInt() as Int)
            args.putParcelableArrayList("songData",SongPlayingFragment.Statified.fetchSongs)
            args.putString("FavBottomBar","success")
            songPlayingFragment.arguments=args
            fragmentManager!!.beginTransaction()
                .replace(R.id.details_fragment,songPlayingFragment)
                .addToBackStack("SongPlayingFragment")
                .commit()
        })
        playPauseButton?.setOnClickListener({
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                playPauseHelper.trackPosition = SongPlayingFragment.Statified.mediaPlayer?.currentPosition as Int
                playPauseHelper.isPlaying = true
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
                SongPlayingFragment.Statified.mediaPlayer?.pause()
            } else {
                playPauseHelper.isPlaying = false
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
                SongPlayingFragment.Statified.mediaPlayer?.seekTo(playPauseHelper.trackPosition)
                SongPlayingFragment.Statified.mediaPlayer?.start()
            }
        })
    }

    fun display_favorites_by_searching(){
        if (favoriteContent?.checkSize() as Int > 0){
            noFavorites?.visibility = View.INVISIBLE
            refreshList = ArrayList<Songs>()
            getListfromDatabase= (favoriteContent as BeatsDatabase).queryDBList()
            var fetchListfromDevice = getSongsFromPhone()
            if (fetchListfromDevice!=null){
                for (i in 0 until fetchListfromDevice.size - 1){
                    for (j in 0 until getListfromDatabase?.size as Int - 1){
                        if (((getListfromDatabase as ArrayList<Songs>)?.get(j).songID)===(fetchListfromDevice?.get(i).songID)){
                            (refreshList as ArrayList<Songs>).add((getListfromDatabase as ArrayList<Songs>)[j])
                        }else{
                        }
                    }
                }
            }

            if ((refreshList as ArrayList<Songs>).size==0){
                recyclerView?.visibility = View.INVISIBLE
                noFavorites?.visibility = View.VISIBLE
            }else{
                var favoriteAdapter= FavoriteAdapter(refreshList as ArrayList<Songs>, activity as Context)
                val mLayoutManager= LinearLayoutManager(activity)
                recyclerView?.layoutManager= mLayoutManager
                recyclerView?.itemAnimator= DefaultItemAnimator()
                recyclerView?.adapter= favoriteAdapter
                recyclerView?.setHasFixedSize(true)
            }
        }else{
            recyclerView?.visibility = View.INVISIBLE
            noFavorites?.visibility = View.VISIBLE
        }

    }
}
