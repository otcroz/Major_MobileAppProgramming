package com.example.ch13

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.icu.util.Output
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings.Global.getString
import android.provider.Settings.Secure.getString
import android.util.Log
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.contentValuesOf
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ch13.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.File
import java.io.OutputStreamWriter

class MainActivity : AppCompatActivity() {
    // 전역 변수로 선언, var로 변경
    var datas:MutableList<String>? = null
    lateinit var adapter : MyAdapter
    lateinit var sharedPreference: SharedPreferences
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 프리퍼런스 값 가져오기
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)
        val bgColor = sharedPreference.getString("color", "")
        binding.rootlayout.setBackgroundColor(Color.parseColor(bgColor))

        // 전체 화면 설정 (SDK 버전 고려)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {// >=30
            window.setDecorFitsSystemWindows(false) // 전체화면으로 설정
            val controller = window.insetsController
            if(controller != null){
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }else{
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        //ActivityResultLauncher 사용하기
        val requestLauncher:ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            //ActivityResultLauncher를 사용하면 인텐트가 되돌아올 때 여기(콜백 함수)로 되돌아옴
            val d3 = it.data!!.getStringExtra("result")?.let{
                datas?.add(it) // datas에 데이터 추가
                adapter.notifyDataSetChanged() // 리사이클러 뷰의 변경을 명시
            }
            //Log.d("mobileApp", d3!!) // !! 널이 아닌 것을 명시
        }

        binding.fab.setOnClickListener{
            val intent = Intent(this, AddActivity::class.java) // 인텐트 생성
            intent.putExtra("data1", "mobile") // 매개변수: 전달되는 값의 이름, 전달하려는 값...(데이터 더 전달 가능)
            intent.putExtra("data2", "app")
            //startActivity(intent) // 인텐트 호출
            //startActivityForResult(intent, 10) // 매개변수: 인텐트, 호출값
            requestLauncher.launch(intent) // ActivityResultLauncher 요청
        }

        datas = mutableListOf<String>()
        // DB 읽어오기
        val db = DBHelper(this).readableDatabase
        var cursor = db.rawQuery("select * from todo_tb", null)

        // 데이터를 배열에 넣기
        while(cursor.moveToNext()){
            datas?.add(cursor.getString(1)) // 2번째 필드에 해당하는 값을 가져옴
        }
        db.close()

        val items = arrayOf<String>("내장")
        binding.fileBtn.setOnClickListener {
            AlertDialog.Builder(this).run{
                setTitle("저장 위치 선택")
                setIcon(android.R.drawable.ic_dialog_info)
                setSingleChoiceItems(items, 1, object: DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        if(p1 == 0){ // 내장 메모리
                            // 저장
                            val file = File(filesDir, "test.txt")
                            val writeStream: OutputStreamWriter = file.writer()
                            writeStream.write("hello android")
                            writeStream.write("$items[p1]")
                            for(i in datas!!.indices) // 인덱스 값 이용
                                writeStream.write(datas!![i]) // i번째 해당하는 data 출력
                            
                            writeStream.flush() // 버퍼에 저장한 내용을 파일에 출력
                            
                            // 데이터 출력하기
                            val readStream:BufferedReader = file.reader().buffered() // 읽어올 버퍼
                            readStream.forEachLine {
                                Log.d("mobileApp", "$it")
                            }

                        }
                    }

                })
                setPositiveButton("선택", null)
                show()
            }
        }

        //액티비티가 비활성 -> 활성되었을 때 Bundle에 저장되었던 datas의 값을 받는다.
        /*datas = savedInstanceState?.let{
            it.getStringArrayList("mydatas")?.toMutableList() // key 값을 통해 값을 얻어옴
        } ?: let{ // null일 때
            mutableListOf<String>() // 리스트 생성 및 선언
        }*/

        // 데이터를 가지고 온 이후(savedInstanceState) 리사이클러 뷰의 어댑터 설정
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter(datas)
        binding.mainRecyclerView.adapter = adapter
        binding.mainRecyclerView.addItemDecoration(
            DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        )
    }

    // datas의 값을 저장해두는 함수
    /*override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putStringArrayList("mydatas", ArrayList(datas)) // Bundle에 ArrayList 값을 저장
    }*/

    // 이 메서드를 통해 인텐트가 돌아옴
    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10 && resultCode== RESULT_OK){

        }
    }*/

    override fun onResume() { // 액티비티가 중단되었다가 다시 실행될 때 호출
        super.onResume()
        val bgColor = sharedPreference.getString("color", "")
        binding.rootlayout.setBackgroundColor(Color.parseColor(bgColor))

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_setting) {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)

        }
        return super.onOptionsItemSelected(item)
    }
}