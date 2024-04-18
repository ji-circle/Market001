package com.example.markettask1

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.markettask1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var items: MutableList<ItemInfo>
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    //데이터 가져오기
    private val myItemsList = MyItemsList.getItemsList()
    lateinit var adapter: MyAdapter
    lateinit var inputItems: MutableList<ItemInfo>

    val channelId = "one-channel"
    val channelName = "My Channel One"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        scrollListener()
        binding.floating1.setOnClickListener {
            //버튼이 눌렸으면 최상단으로 가게 함
            binding.recyclerView.scrollToPosition(0)
        }

        //데이터
        items = myItemsList.myItems.toMutableList()
        //밑에서 이 어댑터를 다시 사용해야 해서... inputItems를 통해 넣는거로 코드 수정함...
        inputItems = items
        //어댑터 준비. 어댑터를 하나 만들어서 그 안에 데이터를 집어넣음
        adapter = MyAdapter(inputItems)
        //리사이클러뷰 객체의 어댑터에 어댑터 집어넣어줌. 연결
        binding.recyclerView.adapter = adapter
        //레이아웃을 어떻게 구성할건지를...
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // MyAdapter에서 받아서...
        //어댑터의 itemClick(MyAdapter에서 ItemClick? 인터페이스 타입의 itemClick)에
        //  MyAdapter.ItemClick 타입의 object를 걸어주는 것
        // MyAdapter에서 onBindViewHolder에서 setOnClickListener 밑의 itemClick?.onClick(it,position)이 발생되면
        //  it은 view(item_recyclerview.xml의 전체. 통.)가 되고,
        //   그게 클릭되면 그것의 view와 position을 onClick의 매개변수로 넣어서 itemClick을 호출시킴.
        //    콜백은 메인의 override fun onClick 부분으로 들어옴
        adapter.itemClick = object : MyAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                //items의 포지션의 thisName을 가져옴.. position은 기본제공
                val name: String = adapter.getItem(position).thisName

                val selectedItem = myItemsList.searchItemByName(name)
                val itemIntent = Intent(this@MainActivity, ItemDetailActivity::class.java)
                itemIntent.putExtra("selected", selectedItem)

                startActivity(itemIntent)
            }
        }

        //하트 누른 경우. 위와 작동방식은 동일함
        adapter.heartClick = object : MyAdapter.HeartClick {
            override fun onClick(view: View, position: Int) {
                val heartItem = adapter.getItem(position)
                //원본 데이터를 변경...
                myItemsList.changeHeartStatus(heartItem, !heartItem.isHeart)
                adapter.notifyItemChanged(position)
            }
        }

        //길게 누른 경우 삭제해야 하니까
        adapter.itemLongClick = object : MyAdapter.ItemLongClick {
            override fun onLongClick(view: View, position: Int): Boolean {
                val longClickedItem = adapter.getItem(position)

                val longBuilder = AlertDialog.Builder(this@MainActivity)
                longBuilder.setTitle("Delete Item")
                    .setMessage("정말 지우시겠습니까?")
                    .setPositiveButton("확인") { dialog, which ->
                        //삭제 처리
                        // 1차 코드에서는 concurrentmodificationexception 발생... delete에 break 추가해줌
                        myItemsList.deleteItemByName(longClickedItem.thisName)
                        updateData()
                        Toast.makeText(
                            this@MainActivity,
                            "${longClickedItem.thisName} 삭제",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .setNegativeButton("취소") { dialog, which ->
                        //아무것도 안하는...
                    }
                val dialog = longBuilder.create()
                dialog.show()
                return true
            }
        }

        //버튼 눌렸을 때 알림 가게
        binding.ibBtnBell.setOnClickListener {
            notification()
        }
    }

    private fun scrollListener() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(-1)) {
                    //스크롤이 최상단에 도달한 경우
                    binding.floating1.hide()
                } else {
                    binding.floating1.show()
                }
            }
        })
    }

    //돌아왔을 때 업데이트된 데이터 반영해주기
    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        updateData()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData() {
        //바뀐 지금 상태의 인스턴스 받아오기
        val updatedItem = MyItemsList.getItemsList().myItems.toMutableList()
        //값 넣어주기
        adapter.mItems = updatedItem
        //변화 알리기
        adapter.notifyDataSetChanged()
    }

    fun notification() {
        //notificationManager를 받아옴
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder
        //채널 만드는 게 8.0 이상 부터니까 버전체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 26 버전 이상

            // 추가한 코드... 사용자 권한 요청 부분. 설정으로 안내함
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                    // 알림 권한이 없다면, 사용자에게 권한 요청
                    // Setting 는 android.provider 로 선택하기
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    }
                    startActivity(intent)
                }
            }

            //채널을 하나 만든다. NotificationChannel로.
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                // 채널에 다양한 정보 설정
                description = "My Channel One Description"

                // 빨간색으로 1, 2 뜨는거...?
                setShowBadge(true)

                //알람 울리게. 링톤
                val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                //오디오 들어가 있는거... 이건 기본 소리니까 바꾸고 싶으면 mp3 다운받아 바꾸기
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()

                //사운드에 오디오 넣기
                setSound(uri, audioAttributes)

                //진동 넣을건지
                enableVibration(true)
            }
            // 채널을 manager를 통해 NotificationManager에 등록
            manager.createNotificationChannel(channel)

            // 채널을 이용하여 builder 생성, builder 통해 채널 아이디 넣어줌
            builder = NotificationCompat.Builder(this, channelId)

        } else {
            // 26 버전(8.0) 이하라면
            builder = NotificationCompat.Builder(this)
        }

        // 알림의 기본 정보... 빌더의 옵션들 ??
        builder.run {
            setSmallIcon(R.drawable.apple)
            //알람 발생 시각 : 현재 시각
            setWhen(System.currentTimeMillis())
            setContentTitle("키워드 알림")
            setContentText("설정한 키워드에 대한 알림이 도착했습니다.")
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
            setAutoCancel(true)
        }
        //알림 실행... permission 추가하기!
        manager.notify(11, builder.build())
    }

    //뒤로가기
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

        AlertDialog.Builder(this)
            .setTitle("뒤로가기 타이틀")
            .setMessage("종료하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->//사용자가 확인을 선택한 경우 앱 종료...
                finish()
            }
            .setNegativeButton("취소") { _, _ ->//사용자가 취소를 선택한 경우 아무 동작도 하지 않음
                //
            }
            .show()
    }
}