package arman.beats.fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutParams.WRAP_CONTENT
import arman.beats.CurrentSongHelper
import arman.beats.R
import arman.beats.Songs
import arman.beats.adapters.MainScreenAdapter
import java.util.*
import kotlin.collections.ArrayList


class MainScreenFragment : Fragment() {
    var playPauseHelper: CurrentSongHelper? = null
    var getSongsList:ArrayList<Songs>?=null
    var nowPlayingBottomBar: RelativeLayout?=null
    var playPauseButton:ImageButton?=null
    var songTitle: TextView?=null
    var visibleLayout: RelativeLayout?=null
    var noSongs: RelativeLayout?=null
    var RecyclerView: RecyclerView?=null
    var myActivity: Activity?=null
    var _mainScreenAdapter: MainScreenAdapter?=null

    object Statified {
        var mMediaPlayer: MediaPlayer? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_main_screen, container,false)
        setHasOptionsMenu(true)
        activity?.title="All Songs"
        visibleLayout= view?.findViewById(R.id.visibleLayout)
        noSongs= view?.findViewById(R.id.noSongs)
        songTitle=view?.findViewById(R.id.songTitleMainScreen)
        nowPlayingBottomBar= view?.findViewById(R.id.hiddenBarMainScreen)
        playPauseButton= view?.findViewById(R.id.playPauseButton)
        RecyclerView= view?.findViewById(R.id.contentMain)
        (nowPlayingBottomBar as RelativeLayout).isClickable = false
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getSongsList= getSongsFromPhone()
        playPauseHelper = CurrentSongHelper()
        val prefs = activity?.getSharedPreferences("action_sort",Context.MODE_PRIVATE)
        val action_sort_ascending = prefs?.getString("action_sort_ascending","true")
        val action_sort_recent = prefs?.getString("action_sort_recent","false")

        if (getSongsList==null){
            visibleLayout?.visibility= View.INVISIBLE
            noSongs?.visibility = View.VISIBLE
        }else{
            _mainScreenAdapter= MainScreenAdapter(getSongsList as ArrayList<Songs>, myActivity as Context)
            val mLayoutManager= LinearLayoutManager(myActivity)
            RecyclerView?.layoutManager= mLayoutManager
            RecyclerView?.itemAnimator=DefaultItemAnimator()
            RecyclerView?.adapter= _mainScreenAdapter
        }

        if (getSongsList!=null){
            if (action_sort_ascending!!.equals("true",true)){
                Collections.sort(getSongsList,Songs.Statified.nameComparator)
            _mainScreenAdapter?.notifyDataSetChanged()
            }else if (action_sort_recent!!.equals("true",true)) {
                Collections.sort(getSongsList, Songs.Statified.dateComparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            }
        }
        bottomBarSetup()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu?.clear()
        inflater!!.inflate(R.menu.main,menu)
        return
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val switcher= item?.itemId
        if (switcher==R.id.action_sort_ascending){
            val editor = myActivity?.getSharedPreferences("action_sort",Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_ascending","true")
            editor?.putString("action_sort_recent","false")
            editor?.apply()
            if (getSongsList!= null){
                Collections.sort(getSongsList,Songs.Statified.nameComparator)
            }
            _mainScreenAdapter?.notifyDataSetChanged()
            return false
        }else if (switcher == R.id.action_sort_recent){
            val editorTwo = myActivity?.getSharedPreferences("action_sort",Context.MODE_PRIVATE)?.edit()
            editorTwo?.putString("action_sort_ascending","false")
            editorTwo?.putString("action_sort_recent","true")
            editorTwo?.apply()
            if (getSongsList!= null){
                Collections.sort(getSongsList,Songs.Statified.dateComparator)
            }
            _mainScreenAdapter?.notifyDataSetChanged()
            return false
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myActivity=context as Activity
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        myActivity=activity
    }

    fun getSongsFromPhone():ArrayList<Songs>{
        var arrayList= ArrayList<Songs>()
        var contentResolver= myActivity?.contentResolver
        var songUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor= contentResolver?.query(songUri, null,null,null,null)
        if (songCursor!= null && songCursor.moveToFirst()){
            val songId= songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle= songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist= songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData= songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            while (songCursor.moveToNext()){
                var currentId= songCursor.getLong(songId)
                var currentTitle= songCursor.getString(songTitle)
                var currentArtist= songCursor.getString(songArtist)
                var currentData= songCursor.getString(songData)
                var currentDate= songCursor.getLong(dateIndex)
                arrayList.add(Songs(currentId,currentTitle,currentArtist,currentData,currentDate))
            }
        }
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
            songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean){
                playPauseHelper?.isPlaying = true
                nowPlayingBottomBar?.visibility = View.VISIBLE
                nowPlayingBottomBar?.layoutParams?.height = WRAP_CONTENT
                nowPlayingBottomBar?.setPadding(0, 11, 0, 11)
                nowPlayingBottomBar?.requestLayout()
            }else{
                nowPlayingBottomBar?.visibility= View.INVISIBLE
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun bottomBarClickHandler(){
        nowPlayingBottomBar?.setOnClickListener({
            Statified.mMediaPlayer = SongPlayingFragment.Statified.mediaPlayer
            val songPlayingFragment= SongPlayingFragment()
            val args= Bundle()
            args.putString("songArtist",SongPlayingFragment.Statified.currentSongHelper?.songArtist)
            args.putString("Path",SongPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putString("songTitle",SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            args.putInt("songId",SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt("songPosition",SongPlayingFragment.Statified.currentSongHelper?.currentPosition?.toInt() as Int)
            args.putParcelableArrayList("songData",SongPlayingFragment.Statified.fetchSongs)
            args.putString("BottomBar","true")
            songPlayingFragment.arguments=args
            fragmentManager!!.beginTransaction()
                .replace(R.id.details_fragment,songPlayingFragment)
                .addToBackStack("MainScreenFragment")
                .commit()
        })
        playPauseButton?.setOnClickListener({
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean){
                playPauseHelper?.trackPosition = SongPlayingFragment.Statified.mediaPlayer?.currentPosition as Int
                playPauseHelper?.isPlaying = true
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
                SongPlayingFragment.Statified.mediaPlayer?.pause()
            } else {
                playPauseHelper?.isPlaying = false
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
                SongPlayingFragment.Statified.mediaPlayer?.seekTo(playPauseHelper!!.trackPosition)
                SongPlayingFragment.Statified.mediaPlayer?.start()
            }
        })
    }

}
