package io.avario.fansolution.android.common.data

import java.net.MalformedURLException

class MalformedHttpUrlException : MalformedURLException {
    constructor() : super()
    constructor(message: String?) : super(message)
}
