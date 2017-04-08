/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, {Component} from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View
} from 'react-native';

import Login from './Login'

const FBSDK = require('react-native-fbsdk');
//const FBSDKCore = require('react-native-fbsdkcore');
const {
  LoginButton,
  AccessToken
} = FBSDK;

//const {
//  AccessToken
//} = FBSDKCore;

export default class SocialDisasters extends Component {
  

  render() {
    return (
      <View>
        <Text>Welcome to the Facebook SDK for React Native!</Text>
        <LoginButton
          publishPermissions={["publish_actions"]}
          onLoginFinished={
            (error, result) => {
              if (error) {
                alert("Login failed with error: " + result.error);
              } else if (result.isCancelled) {
                alert("Login was cancelled");
              } else {
                AccessToken.getCurrentAccessToken().then(
                  (data) => {
                    alert(data.accessToken.toString())
                  }
                )
              }
            }
          }
          onLogoutFinished={() => alert("User logged out")}/>
      </View>
    );
  }
}




AppRegistry.registerComponent('SocialDisasters', () => SocialDisasters);
