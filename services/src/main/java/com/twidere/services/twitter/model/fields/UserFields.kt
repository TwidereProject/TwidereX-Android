/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
 
package com.twidere.services.twitter.model.fields

enum class UserFields(val value: String) {
    created_at("created_at"),
    description("description"),
    entities("entities"),
    id("id"),
    location("location"),
    tw_name("name"),
    pinned_tweet_id("pinned_tweet_id"),
    profile_image_url("profile_image_url"),
    tw_protected("protected"),
    public_metrics("public_metrics"),
    url("url"),
    username("username"),
    verified("verified"),
    withheld("withheld"),
}
