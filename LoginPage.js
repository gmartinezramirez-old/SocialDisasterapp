import React, { Component } from 'react';
import {
    AppRegistry,
    StyleSheet,
    Text,
    View,
    Image,
    TouchableHighlight,
    TextInput,
    Navigator,
} from 'react-native';

////////////////////////////////////////// DEFINITION COMPOSANTS  /////////////////////////
const FBSDK = require('react-native-fbsdk');
const {
    LoginButton,
} = FBSDK;

var Login = React.createClass({
    render: function() {
        return (
            <View>
                <LoginButton
                    publishPermissions={["publish_actions"]}
                    onLoginFinished={
                        (error, result) => {
                            if (error) {
                                alert("Login failed with error: " + result.error);
                            } else if (result.isCancelled) {
                                alert("Login was cancelled");
                            } else {
                                alert("Login was successful with permissions: " + result.grantedPermissions)
                            }
                        }
                    }
                    onLogoutFinished={() => alert("User logged out")}/>
            </View>
        );
    }
});
//////////////////////////////////////////// CLASSE PRINCIPALE //////////////////////////

export default class LoginPage extends Component {

    render() {

        return (

            <View>
               <Text> fb login</Text>
                <Login/>
            </View>
        );
    }
}

// Définition de tous les styles de la page.
const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#F5FCFF',
    },
    welcome: {
        fontSize: 20,
        textAlign: 'center',
        margin: 10,
    },
    instructions: {
        textAlign: 'center',
        color: '#333333',
        marginBottom: 5,
    },
    searchInput: {
        height: 40,
        width:250,
        fontSize:18,
        borderWidth:1,
        color:'black',
    },
    button: {
        height:40,
        backgroundColor:'blue',
        borderColor:'blue',
    },
    buttonText: {
        fontSize:18,
        color:'white',
        alignSelf:'center'
    }
});
