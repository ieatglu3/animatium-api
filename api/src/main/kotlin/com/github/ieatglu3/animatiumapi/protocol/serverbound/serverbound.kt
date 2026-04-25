package com.github.ieatglu3.animatiumapi.protocol.serverbound

import com.github.ieatglu3.animatiumapi.protocol.animatiumIdentifier
import com.github.ieatglu3.payloadcrafter.CustomPayloadType
import com.github.ieatglu3.payloadcrafter.Deserializer
import com.github.ieatglu3.payloadcrafter.ServerboundCustomPayload
import com.github.ieatglu3.payloadcrafter.WrappedByteBuf
import java.util.Optional

class PayloadServerboundInfo(val version: Double, val devVersion: Optional<String>) : ServerboundCustomPayload(TYPE)
{
  companion object {
    @JvmStatic
    val TYPE = CustomPayloadType.serverboundPlay(
      PayloadServerboundInfo::class.java,
      animatiumIdentifier("info"),
      object : Deserializer<PayloadServerboundInfo>
      {
        override fun read(buf: WrappedByteBuf): PayloadServerboundInfo
        {
          val version = buf.readDouble()
          val devVersion = buf.readOptional { it.readUTF() }
          return PayloadServerboundInfo(version, devVersion)
        }
      }
    )
  }
}