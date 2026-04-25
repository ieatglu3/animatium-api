package com.github.ieatglu3.animatiumapi.protocol

import com.github.ieatglu3.animatiumapi.protocol.clientbound.PayloadClientboundSetServerFeatures
import com.github.ieatglu3.animatiumapi.protocol.serverbound.PayloadServerboundInfo
import com.github.ieatglu3.payloadcrafter.CustomPayloadRegistry
import com.github.ieatglu3.payloadcrafter.CustomPayloadRegistryBuilder
import com.github.ieatglu3.payloadcrafter.Identifier

fun animatiumIdentifier(path: String): Identifier = Identifier.of("animatium", path)

object AnimatiumPayloadRegistry
{
  @JvmStatic
  val handle: CustomPayloadRegistry = CustomPayloadRegistryBuilder()
    .register(PayloadClientboundSetServerFeatures.TYPE)
    .register(PayloadServerboundInfo.TYPE)
    .build()
}