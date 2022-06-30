package com.autocrypttest.customEx

class UserNotMatchException(tokenName: String, requestName: String) : IllegalArgumentException() {
    override val message = "사용자 정보가 일치하지 않습니다."

    init {
        super("$message$tokenName!=$requestName")
    }
}