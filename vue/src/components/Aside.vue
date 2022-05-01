<template>
  <div>
      <el-menu
          style="width: 200px;min-height: calc(100vh - 50px)"
          :default-active="$route.path"
          router
      >
        <div  v-for="m in user.permissions" :key="m.id">
          <el-menu-item :index="m.path" v-if="m.name !== 'Person' && m.name !== 'Password' ">
            <i :class="m.icon"></i>  {{ m.comment }}
          </el-menu-item>
        </div>

        <!--<el-menu-item index="2">-->
        <!--  <i class="el-icon-menu"></i>-->
        <!--  <span slot="title">导航二</span>-->
        <!--</el-menu-item>-->
        <!--<el-menu-item index="4">-->
        <!--  <i class="el-icon-setting"></i>-->
        <!--  <span slot="title">导航四</span>-->
        <!--</el-menu-item>-->
      </el-menu>
  </div>
</template>


<script>
import request from "@/utils/request";

export default {
  name: "Aside",
  created() {
    // console.log(this.$route.path)
    let userStr = sessionStorage.getItem("user") || "{}"
    this.user = JSON.parse(userStr)

  },
  data() {
    return{
      user: {}
    }
  },
  methods: {
    matchState(string = "") {
      return string !== "/person" && string !== "/password";
    }
  }
}
</script>

<style scoped>

</style>