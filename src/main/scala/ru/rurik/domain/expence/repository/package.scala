package ru.rurik.domain.expence

import zio.Has

package object repository {
  type ExpenceRepository = Has[ExpenceRepository.Service]
}
