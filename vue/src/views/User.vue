<template>
  <div style="padding: 10px">
    <!--<img alt="Vue logo" src="../assets/logo.png">-->
    <!--CRUD-->
    <div style="margin: 5px 0">
      <el-button type="primary"  @click="add">新增</el-button>
      <el-upload
          action="http://localhost:9090/user/import"
          :on-success="handleUploadSuccess"
          :show-file-list=false
          :limit="1"
          accept='.xlsx'
          style="display: inline-block; margin: 0 10px"
      >
        <el-button type="primary">导入</el-button>
      </el-upload>
      <el-button type="primary" @click="exportUser">导出</el-button>
    </div>

    <!--搜索-->
    <div style="margin: 10px 0">
      <el-input v-model="search" placeholder="请输入关键字" style="width: 25%" clearable></el-input>
      <el-button type="primary" style="margin-left: 7px" @click="load" >查询</el-button>
    </div>

    <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%">
      <el-table-column
          prop="id"
          label="ID"
          sortable>
      </el-table-column>
      <el-table-column
          prop="username"
          label="用户名">
      </el-table-column>
      <el-table-column
          prop="nick_name"
          label="昵称">
      </el-table-column>
      <el-table-column
          prop="age"
          label="年龄">
      </el-table-column>
      <el-table-column
          prop="sex"
          label="性别">
      </el-table-column>
      <el-table-column
          prop="address"
          label="地址">
      </el-table-column>

      <el-table-column label="角色列表" width="300">
        <template #default="scope">
          <el-select v-model="scope.row.roles" multiple placeholder="请选择" style="width: 80%">
            <el-option v-for="item in roles" :key="item.id" :label="item.comment" :value="item.id"></el-option>
          </el-select>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="400">
        <template #default="scope" >
          <el-button size="mini" type="primary" @click="handleChange(scope.row)">保存角色信息</el-button>
          <el-button size="mini" type="success" plain @click="showBooks(scope.row.bookList)">查看图书列表</el-button>
          <el-button size="mini" type="primary" plain @click="handleEdit(scope.row)">编辑</el-button>
          <el-popconfirm @confirm="handDelete(scope.row.id)"
              title="确定删除？">
            <template #reference  >
              <el-button size="mini" type="danger" style="margin-left: 10px">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin: 10px 0">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[5, 10, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>


    <el-dialog title="用户拥有的图书列表" :visible.sync="bookVis" width="30%">
      <el-table :data="bookList" stripe border>
        <el-table-column prop="id" label="ID"></el-table-column>
        <el-table-column prop="name" label="名称"></el-table-column>
        <el-table-column prop="price" label="价格"></el-table-column>
      </el-table>
    </el-dialog>

      <el-dialog title="提示" :visible.sync="dialogVisible" width="30%">
        <el-form label-width="80px">
          <el-form-item :moodel="form" label="用户名">
            <el-input v-model="form.username" style="width: 83%"></el-input>
          </el-form-item>
          <el-form-item :moodel="form" label="昵称">
            <el-input v-model="form.nick_name" style="width: 83%"></el-input>
          </el-form-item>
          <el-form-item :moodel="form" label="年龄">
            <el-input v-model="form.age" style="width: 83%"></el-input>
          </el-form-item>
          <el-form-item :moodel="form" label="性别">
            <el-radio v-model="form.sex" label="男">男</el-radio>
            <el-radio v-model="form.sex" label="女">女</el-radio>
            <el-radio v-model="form.sex" label="未知">未知</el-radio>
          </el-form-item>
          <el-form-item :moodel="form" label="地址">
            <el-input type="textarea" v-model="form.address" style="width: 83%"></el-input>
          </el-form-item>
          <el-form-item label="账户余额">
            <el-input v-model="form.account" style="width: 80%"></el-input>
          </el-form-item>
          </el-form>

        <span slot="footer" class="dialog-footer" >
          <el-button @click="dialogVisible = false" >取 消</el-button>
          <el-button type="primary" @click="save" >确 定</el-button>
        </span>
      </el-dialog>
    </div>
</template>

<style>
body {
  margin: 0;
}
</style>

<script>


import request from "@/utils/request";

export default {
  name: 'User',
  components: {

  },
  data() {
    return{
      form: {},
      dialogVisible: false,
      bookVis: false,
      loading: true,
      search: '',
      currentPage: 1,
      pageSize: 10,
      total: 0,
      tableData: [],
      bookList: [],
      roles: []
    }
  },
  created() {
    this.load()
  },
  methods: {
    handleChange(row) {
      request.put("/user/changeRole", row).then(res => {
        if (res.code === '0') {
          this.$message.success("更新成功")
          if (res.data) {
            this.$router.push("/login")
          }
        }
      })
    },
    showBooks(books) {
      this.bookVis = true
      this.bookList = books
    },
    load() {
      request.get("/user",{
        params: {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          search: this.search
        }
      }).then(res => {
        console.log(res)
        this.tableData = res.data.records
        this.total = res.data.total
        this.loading = false
      })
      request.get("/role/all").then(res => {
        this.roles = res.data
      })
    },
    handleUploadSuccess(res) {
      if (res.code === "0") {
        this.$message.success("导入成功")
        this.load()
      }
    },
    exportUser() {
      location.href = "http://localhost:9090/user/export";
    },
    add() {
      this.dialogVisible = true
      this.form = {}
    },
    save() {
      if (this.form.id){    //更新
        request.put("/user",this.form).then(res => {
          console.log(res)
          if(res.code === '0'){
            this.$message({
              type: "success",
              message: "更新成功"
            })
          } else {
            this.$message({
              type: "error",
              message: res.msg
            })
          }
        })
      } else  {   //新增
        request.post("/user",this.form).then(res => {
          console.log(res)
          if(res.code === '0'){
            this.$message({
              type: "success",
              message: "新增成功"
            })
          } else {
            this.$message({
              type: "error",
              message: res.msg
            })
          }
          this.load()   //刷新表格的数据
          this.dialogVisible = false    //关闭弹窗
        })
      }
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogVisible = true
    },
    handDelete(id) {
      console.log(id)
      request.delete("/user/" + id).then(res => {
        if(res.code === '0'){
          this.$message({
            type: "success",
            message: "删除成功"
          })
        } else {
          this.$message({
            type: "error",
            message: res.msg
          })
        }
        this.load()   //刷新表格的数据
      })
    },
    handleSizeChange(pageSize) {    //改变当前每页的个数触发
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {   //改变当前页码触发
      this.currentPage = pageNum;
      this.load()
    }
  }
}
</script>
