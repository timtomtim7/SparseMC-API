package blue.sparse.minecraft.commands.parsing.impl

import blue.sparse.minecraft.commands.parsing.Parser
import blue.sparse.minecraft.core.extensions.server

val worldParser = Parser.of({ true }, server::getWorld)