<template>
  <div style="height: 50px; line-height: 50px; border-bottom: 1px solid #ccc; display: flex">
    <div style="width: 150px ; padding-left: 30px ; font-weight: bold ; color: dodgerblue ">后台管理</div>
    <div style="flex: 1"></div>
    <div style="width: 250px">
      <el-dropdown>
        <span class="el-dropdown-link" >
            <!--<el-avatar :size="30" :src="user.avatar" style="position: relative; top: 10px"></el-avatar>-->
           <el-avatar :size="30" :src="user.avatar" style="position: relative; top: 10px"></el-avatar>
          {{user.nick_name}}
          <i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click.native="$router.push('/person')">个人信息</el-dropdown-item>
          <el-dropdown-item @click.native="$router.push('/login')">登出</el-dropdown-item>
          <el-dropdown-item @click.native="$router.push('/password')">修改密码</el-dropdown-item>
        </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script>

export default {
  name: "Header",
  data() {
    return {
      user: {}
    }
  },
  created() {
    let str = sessionStorage.getItem("user") || "{}"
    this.user = JSON.parse(str)
    // console.log(sessionStorage.getItem("user"))
  }
}
</script>


<style scoped>

</style>