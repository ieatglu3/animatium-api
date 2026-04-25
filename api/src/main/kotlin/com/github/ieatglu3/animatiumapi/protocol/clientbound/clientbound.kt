package com.github.ieatglu3.animatiumapi.protocol.clientbound

import com.github.ieatglu3.animatiumapi.ServerFeature
import com.github.ieatglu3.animatiumapi.protocol.AnimatiumPayloadRegistry
import com.github.ieatglu3.animatiumapi.protocol.animatiumIdentifier
import com.github.ieatglu3.payloadcrafter.ClientboundCustomPayload
import com.github.ieatglu3.payloadcrafter.CustomPayloadType
import com.github.ieatglu3.payloadcrafter.Deserializer
import com.github.ieatglu3.payloadcrafter.WrappedByteBuf
import java.util.BitSet
import java.util.EnumSet

class PayloadClientboundSetServerFeatures(val features: Collection<ServerFeature>) : ClientboundCustomPayload(TYPE)
{
  override fun write(buffer: WrappedByteBuf)
  {
    val bitSet = BitSet(this.features.size)
    for (feature in this.features)
      bitSet.set(feature.numericID())
    buffer.writeBytes(bitSet.toByteArray())
  }

  companion object {
    @JvmStatic
    val TYPE = CustomPayloadType.clientboundPlay(
      PayloadClientboundSetServerFeatures::class.java,
      animatiumIdentifier("set_server_features"),
      object : Deserializer<PayloadClientboundSetServerFeatures>
      {
        override fun read(buf: WrappedByteBuf): PayloadClientboundSetServerFeatures
        {
          val bitSet = BitSet.valueOf(buf.readBytes())
          val features = EnumSet.noneOf(ServerFeature::class.java)
          for (feature in ServerFeature.entries)
            if (bitSet.get(feature.numericID()))
              features.add(feature)
          return PayloadClientboundSetServerFeatures(features)
        }
      }
    )
  }
}