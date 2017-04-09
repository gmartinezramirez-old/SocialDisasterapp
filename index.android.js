
import React, {Component} from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  TextInput,
  View
} from 'react-native';

import Login from './Login'

const FBSDK = require('react-native-fbsdk');

const {
  LoginButton,
  AccessToken
} = FBSDK;

export default class SocialDisasters extends Component {
  
  constructor(props) {
    super(props);
    this.state = { text: 'Password' };
  }


  render() {
    return (
      <View>
        <Text>Welcome to the Social Disaster app!</Text>
        <TextInput
        style={{height: 40, borderColor: 'gray', borderWidth: 1}}
        onChangeText={(text) => this.setState({text})}
        value={this.state.text}
        />
        <LoginButton
          publishPermissions={["publish_actions"]}
          onLoginFinished={
            (error, result) => {
              if (error) {
                alert("Login failed with error: " + result.error);
              } else if (result.isCancelled) {
                alert("Login was cancelled");
              } else {
                // obtain access token
                AccessToken.getCurrentAccessToken().then(
                  (data) => {
                    var userToken=data.accessToken.toString();
                    var userId =data.getUserId().toString(); 
                    alert(userToken)
                    //alert(userId)
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
