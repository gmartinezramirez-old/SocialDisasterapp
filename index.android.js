

import ibrdtn from './ibrdtn';
import React, {Component} from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  TextInput,
  View,
  Button
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
    ibrdtn.init(); 
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
        <Button onPress={onButtonPress} title="Press Purple" color="#841584" accessibilityLabel="Learn more about purple" />
      </View>
    );
  }
}


const onButtonPress = () =>{
    console.log("boton send");
    //ibrdtn.send("dtn://android-913d8a47.dtn");
    ibrdtn.send("dtn://android-7e424bc4.dtn");
}



AppRegistry.registerComponent('SocialDisasters', () => SocialDisasters);
