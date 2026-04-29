package com.github.ieatglu3.animatiumapi.protocol.clientbound

import com.github.ieatglu3.animatiumapi.ServerFeature
import com.github.ieatglu3.animatiumapi.protocol.animatiumIdentifier
import com.github.ieatglu3.payloadcrafter.ClientboundCustomPayload
import com.github.ieatglu3.payloadcrafter.CustomPayloadType
import com.github.ieatglu3.payloadcrafter.Deserializer
import com.github.ieatglu3.payloadcrafter.WrappedByteBuf
import java.util.BitSet
import java.util.EnumSet

object PayloadClientboundSetServerFeatures
{
  fun serialize(features: Collection<ServerFeature>): ByteArray
  {
    val bitSet = BitSet(features.size)
    for (feature in features)
      bitSet.set(feature.numericID())
    return bitSet.toByteArray()
  }
  fun readEnumSet(buf: WrappedByteBuf): EnumSet<ServerFeature>
  {
    val bitSet = BitSet.valueOf(buf.readBytes())
    val features = EnumSet.noneOf(ServerFeature::class.java)
    for (feature in ServerFeature.entries)
      if (bitSet.get(feature.numericID()))
        features.add(feature)
    return features
  }
}

class PayloadClientboundSetServerFeaturesV2(val features: Collection<ServerFeature>) : ClientboundCustomPayload(TYPE)
{
  override fun write(buffer: WrappedByteBuf) = buffer.writeBytes(PayloadClientboundSetServerFeatures.serialize(this.features))
  companion object {
    @JvmStatic
    val TYPE = CustomPayloadType.clientboundPlay(
      PayloadClientboundSetServerFeaturesV2::class.java,
      animatiumIdentifier("set_features"),
      object : Deserializer<PayloadClientboundSetServerFeaturesV2>
      {
        override fun read(buf: WrappedByteBuf): PayloadClientboundSetServerFeaturesV2
          = PayloadClientboundSetServerFeaturesV2(PayloadClientboundSetServerFeatures.readEnumSet(buf))
      }
    )
  }
}

class PayloadClientboundSetServerFeaturesV3(val features: Collection<ServerFeature>) : ClientboundCustomPayload(TYPE)
{
  override fun write(buffer: WrappedByteBuf) = buffer.writeBytes(PayloadClientboundSetServerFeatures.serialize(this.features))
  companion object {
    @JvmStatic
    val TYPE = CustomPayloadType.clientboundPlay(
      PayloadClientboundSetServerFeaturesV3::class.java,
      animatiumIdentifier("set_server_features"),
      object : Deserializer<PayloadClientboundSetServerFeaturesV3>
      {
        override fun read(buf: WrappedByteBuf): PayloadClientboundSetServerFeaturesV3
          = PayloadClientboundSetServerFeaturesV3(PayloadClientboundSetServerFeatures.readEnumSet(buf))
      }
    )
  }
}