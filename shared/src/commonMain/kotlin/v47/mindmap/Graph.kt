package v47.mindmap

import org.koin.dsl.module
import v47.mindmap.connections.ConnectionsRepository
import v47.mindmap.connections.StaticConnectionsRepository
import v47.mindmap.thought.StaticThoughtRepository
import v47.mindmap.thought.ThoughtsRepository

val commonDependencies = module {
    single<ConnectionsRepository> { StaticConnectionsRepository }
    single<ThoughtsRepository> { StaticThoughtRepository }
}