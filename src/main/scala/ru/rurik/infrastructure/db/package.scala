package ru.rurik.infrastructure

import zio.Has

package object db {
  type DatabaseProvider = Has[DatabaseProvider.Service]
}
