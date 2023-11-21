package com.example.firebasetest.lhs.imageShareApp.recycler

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebasetest.lhs.MyApplication
import com.example.firebasetest.lhs.databinding.ItemMainBinding
import com.example.firebasetest.lhs.imageShareApp.model.ItemData

// 뷰홀더 설정.
// 아이템 요소의 뷰를 설정하기. -> 목록요소 뷰 -> 메뉴 아이템 뷰 구성. 백

class  MyViewHolder(val binding : ItemMainBinding) : RecyclerView.ViewHolder(binding.root)
class MyAdapter (val context: Context, val itemList: MutableList<ItemData>) // 이게 액티비티가 아니라서 주 생성자를 구성
    : RecyclerView.Adapter<MyViewHolder>() {
    // 목록 요소에, 데이터를 연결시켜주는 역할
    // 뷰홀더도 있음.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MyViewHolder(ItemMainBinding.inflate(layoutInflater))
    }

    // 넘어온 전체 데이터 크기
    override fun getItemCount(): Int {
        return itemList.size
    }

    // 뷰와 데이터 붙이기 작업(연동)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = itemList.get(position)

        holder.binding.run {
            emailResultView.text = data.email
            dateResultView.text = data.date
            contentResultView.text = data.content
        }

        // 스토리지에서 이미지 불러와서, Glide로 출력하기
        val imgRef = MyApplication.storage.reference
            // 사진이 한장이라서, 게시글의 id(자동생성된 값) 이용하고 있음.
            .child("AndroidImageShareApp/${data.docId}.jpg")
        // 다운로드, 스토리지에서, 이미지의 저장소의 URL 가져오기.
        imgRef.downloadUrl
            .addOnCompleteListener{
                // 성공 시, 수행할 콜백 함수, task 안에, 이미지의 url이 담겨있음.
                task ->
                if(task.isSuccessful) {
                    // with 동작을 해당 activity, 등 context로 대체 .
                    Glide.with(context)
                        // URL 주소로 이미지 불러오기
                        .load(task.result)
                    // 결과 뷰에 이미지 넣기
                        .into(holder.binding.imageResultView)
                }
            } // addOnCompleteListener

        // 삭제 기능.
        // 스토어, 이미지 삭제.
        holder.binding.deleteBtn.setOnClickListener {
            MyApplication.db.collection("AndroidImageShareApp")
                .document("${data.docId}")
                .delete()
                .addOnSuccessListener {
                    Log.d("lhs", "DocumentSnapshot successfully deleted!")
                    Toast.makeText(context,"삭제 성공", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener { e ->
                    Log.w("lhs", "Error deleting document", e)
                    Toast.makeText(context,"삭제 실패", Toast.LENGTH_SHORT).show()
                }
        }

    } // onBindViewHolder

} //MyAdapter