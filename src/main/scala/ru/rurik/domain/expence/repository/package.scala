package ru.rurik.expence

import zio.Has

package object repository {
  type ExpenceRepository = Has[ExpenceRepository.Service]
}
