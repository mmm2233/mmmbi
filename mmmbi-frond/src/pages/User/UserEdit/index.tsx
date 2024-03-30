import {useModel} from '@umijs/max';
import {  Button, Card, Col, Form,Input, InputNumber,message, Row } from 'antd';
import React, {useEffect, useState} from 'react';
import {getDailyLoginPointUsingGet,getUserPointUsingGet} from "@/services/mmmbi/pointController";
import {request} from "@/app";
import { getUserVoByIdUsingGet, updateMyUserUsingPost } from "@/services/mmmbi/userController";


const UserEdit: React.FC = () => {
  const {initialState} = useModel('@@initialState');
  const {currentUser} = initialState ?? {};
  const [userInfo, setUserInfo] = useState<API.UserVO>();
  const [creditInfo, setCreditInfo] = useState<number>();
  const [amountInfo, setAmountInfo] = useState<number>();
  const initUserParams = {
    id: currentUser?.id
  };
  const [userInfoParams, setUserInfoParams] = useState<API.getUserVOByIdUsingGETParams>({...initUserParams});
  const [submitting, setSubmitting] = useState<boolean>(false);

  const userInit = {
    userName: "admin"
  }

  /**
   * 获取用户完整信息
   */
  const loadData = async () => {
    try {
      const res = await getUserVoByIdUsingGet(userInfoParams);
      creditTotal();
      if (res.data) {
        console.log("res", res.data)
        setUserInfo(res.data ?? userInit)
        console.log("user d", userInfo)
      } else {
        message.error('获取我的信息失败');
      }
    } catch (e: any) {
      message.error('获取我的信息失败，' + e.message);
    }
  };

  /**
   * 获取积分
   */
  const creditTotal = async () => {
    try {
      console.log("userInfoParams", userInfoParams.id)
      const res = await getUserPointUsingGet({
        userId :userInfoParams.id,
      });
      if (res.data) {
        console.log("res", res.data)
        setCreditInfo(res.data.remainingPoints ?? 0)
        console.log("user d", userInfo)
      } else {
        message.error('获取我的积分失败');
      }
    } catch (e: any) {
      message.error('获取我的积分失败，' + e.message);
    }
  };

  useEffect(() => {
    loadData()
    console.log("user", userInfo)
  }, [])

  /**
   * 提交
   * @param values
   */
  const onFinish = async (values: any) => {
    // 避免重复提交
    if (submitting) {
      return;
    }
    setSubmitting(true);
    // 对接后端，上传数据
    const params = {
      ...values,
      file: undefined,
    };
    try {
      const res = await getDailyLoginPointUsingGet(params);
      if (!res?.data) {
        message.error('更新失败');
      } else {
        message.success('更新成功');
        console.log(res.data)
      }
    } catch (e: any) {
      message.error('分析失败，' + e.message);
    }
    setSubmitting(false);
  };

  /**
   * 签到
   * @param values
   */
  const signDaily = async () => {

    setSubmitting(true);
    try {
      const res = await getDailyLoginPointUsingGet();
      if (!res?.data) {
        message.error('签到失败，今天已签到');
      } else {
        message.success('签到成功');
        console.log(res.data)
      }
    } catch (e: any) {
      message.error('签到失败，' + e.message);
    }
    setSubmitting(false);
  };
  /**
   * 充值金额
   * @param value
   */
  const onChange = (value: number | null) => {
    if (value === null){
      message.error('请输入正确金额');
      return
    }
    setAmountInfo(value)
  };
  /**
   * 充值
   * @param values
   */
  const payCredit = async () => {
    setSubmitting(true);
    if (amountInfo === undefined){
      message.error('请输入正确金额');
      return
    }
    window.open(request.baseURL+"/api/alipay/pay?subject=充值积分&totalAmount="+amountInfo)
    setSubmitting(false);
  };
  // @ts-ignore
  return (
    <>{userInfo != undefined &&
      <Row gutter={[16, { xs: 8, sm: 16, md: 24, lg: 32 }]} >
        <Col span={12}>
          <Card title={"个人信息"} >
            <Form name="updateUser" labelAlign="left" labelCol={{span: 4}}
                  wrapperCol={{span: 16}} id={"user"} initialValues={userInfo}
                  onFinish={onFinish}>
              <Form.Item name={"userName"} label="用户昵称">
                <Input/>
              </Form.Item>
              <Form.Item label="更新">
                <Button type="primary" htmlType="submit" loading={submitting} disabled={submitting}>
                  更新
                </Button>
              </Form.Item>
            </Form>
          </Card>

        </Col>
        <Col span={12}>
          <Card title={"头像"}>

          </Card>
        </Col>
        <Col span={12}>
          <Card title={"我的积分"}>
            <span>
              当前积分为：{creditInfo}
              <Button type={"primary"} onClick={signDaily} style={{left:'5px'}}  disabled={submitting}>每日签到</Button>
            </span>
          </Card>
        </Col>
        <Col span={12}>
          <Card title={"充值积分"}>
              充值金额：
              <InputNumber min={1} max={100} defaultValue={0} value={amountInfo} onChange={onChange} />
              <Button type={'primary'} danger onClick={payCredit} style={{left:'5px'}}  disabled={submitting}>充值</Button>
          </Card>
        </Col>
      </Row>
    }
    </>
  );
};
export default UserEdit;

