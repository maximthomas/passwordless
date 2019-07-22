/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Base64 } from './base64'

const settings = {
    host: null,
    apiUrl: null,
};

function bufferDecode(value) {
    return Uint8Array.from(atob(value), c => c.charCodeAt(0));
}

function bufferEncode(value) {
    return Base64.fromByteArray(value)
        .replace(/\+/g, "-")
        .replace(/\//g, "_")
        .replace(/=/g, "");
}


function processError(e) {
    console.log(e.toString());
}

function startRegistration(login) {
    const targetUrl = settings.apiUrl + 'register/challenge/' + login;
    fetch(targetUrl, {
        credentials: 'include'
    })
        .then((res) => {
            res.json().then((challenge) => processRegisterChallenge(challenge)).catch(processError);
        })
        .catch(processError);
}

function processRegisterChallenge(challenge) {
    console.log('received message ' + JSON.stringify(challenge));
    challenge.challenge = bufferDecode(challenge.challenge.value);
    challenge.user.id = bufferDecode(challenge.user.id);
    console.log('converted message ' + JSON.stringify(challenge));

    navigator.credentials.create({
        publicKey: challenge,
    }).then((credential) => {
        register(credential);
    }).catch((e) => {
            console.log(e.toString());
        }
    );
}

function register(credential) {

    const attestationObject = new Uint8Array(credential.response.attestationObject);
    const clientDataJSON = new Uint8Array(credential.response.clientDataJSON);
    const rawId = new Uint8Array(credential.rawId);

    const postData = {
        id: credential.id,
        rawId: bufferEncode(rawId),
        type: credential.type,
        response: {
            attestationObject: bufferEncode(attestationObject),
            clientDataJSON: bufferEncode(clientDataJSON),
        },
    };
    console.log('registering credentials ', postData);
    const targetUrl = settings.apiUrl + 'register/credential/';
    fetch(targetUrl, {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(postData),
    }).then(res => {
        console.log(res)
    }).catch(processError);
}

function startLogin(login) {
    const targetUrl = settings.apiUrl + 'login/challenge/' + login;
    fetch(targetUrl, {credentials: 'include'})
        .then((res) => {
            res.json().then((challenge) => processLoginChallenge(challenge)).catch(processError);
        })
        .catch(processError);
}

function processLoginChallenge(challenge) {

    challenge.challenge = bufferDecode(challenge.challenge.value);
    challenge.allowCredentials.forEach(function (allowCredential, i) {
            allowCredential.id = bufferDecode(allowCredential.id);
        }
    );
    console.log('login challenge', challenge);

    navigator.credentials.get({
        publicKey: challenge,
    }).then((assertion) => {
        assert(assertion);
    }).catch((e) => {
            console.log(e.toString());
        }
    )
}

function assert(assertion) {

    console.log('assertion ',assertion);

    const authenticatorData = new Uint8Array(assertion.response.authenticatorData);
    const clientDataJSON = new Uint8Array(assertion.response.clientDataJSON);
    const signature = new Uint8Array(assertion.response.signature);
    const userHandle = new Uint8Array(assertion.response.userHandle);
    const rawId = new Uint8Array(assertion.rawId);


    const postData = {
        id: assertion.id,
        rawId: bufferEncode(rawId),
        type: assertion.type,
        response: {
            authenticatorData: bufferEncode(authenticatorData),
            clientDataJSON: bufferEncode(clientDataJSON),
            signature: bufferEncode(signature),
            userHandle: bufferEncode(userHandle),
        },
    };


    const targetUrl = settings.apiUrl + 'login/credential/';

    fetch(targetUrl, {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(postData),
    }).then(res => {
        console.log(res)
    }).catch(processError);
}



function init(initSettings) {
    settings.host = initSettings.host;
    settings.apiUrl = settings.host + '/webauthn/v1/';
}

export {startRegistration, startLogin, init}