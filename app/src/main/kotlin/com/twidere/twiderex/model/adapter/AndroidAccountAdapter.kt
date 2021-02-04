/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.model.adapter

import android.accounts.Account
import com.twidere.twiderex.model.JsonAccount
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object AndroidAccountSerializer : KSerializer<Account> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Account", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Account {
        return decoder.decodeSerializableValue(JsonAccount.serializer()).let {
            Account(it.name, it.type)
        }
    }

    override fun serialize(encoder: Encoder, value: Account) {
        encoder.encodeSerializableValue(JsonAccount.serializer(), JsonAccount(value.name, value.type))
    }
}
