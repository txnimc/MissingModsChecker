plugins {
	id("toni.blahaj")
}

blahaj {
	config {	}
	setup {
		txnilib("1.0.23")
		forgeConfig()
		conditionalMixin()
	}
}
