import request from '@/utils/request'

// 查询作业列表
export function listWork(query) {
  return request({
    url: '/datafactory/work/list',
    method: 'get',
    params: query
  })
}

// 查询作业详细
export function getWork(workId) {
  return request({
    url: '/datafactory/work/' + workId,
    method: 'get'
  })
}

// 新增作业
export function addWork(data) {
  return request({
    url: '/datafactory/work',
    method: 'post',
    data: data
  })
}

// 修改作业
export function updateWork(data) {
  return request({
    url: '/datafactory/work',
    method: 'put',
    data: data
  })
}

// 删除作业
export function delWork(workId) {
  return request({
    url: '/datafactory/work/' + workId,
    method: 'delete'
  })
}
